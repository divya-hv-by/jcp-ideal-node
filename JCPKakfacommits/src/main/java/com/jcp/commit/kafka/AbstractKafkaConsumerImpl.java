package com.jcp.commit.kafka;


import com.jcp.commit.config.KafkaConsumerConfiguration;
import com.jcp.commit.config.KafkaConsumerConfigurationProperties;
import com.jcp.commit.config.KafkaProducerConfiguration;
import lombok.Getter;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.*;
import org.springframework.kafka.listener.ContainerProperties.AckMode;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.jcp.commit.config.KafkaConstants.*;

public abstract class AbstractKafkaConsumerImpl<K, V> {

  @Value("${kafka.consumer.enabled:true}")
  private Boolean kafkaConsumerEnabled;

  @Value("${kafka.consumer.dlq-publish.enabled:false}")
  private Boolean deadLetterQueuePublishEnabled;

  @Value("${kafka.region:}")
  private String region;

  @Value("${kafka.customer-name:}")
  private String customerName;

  @Value("${kafka.fixed-back-off.interval}")
  private long fixedBackOffInterval;

  @Value("${kafka.fixed-back-off.max-attempts}")
  private long fixedBackOffMaxAttempts;

  @Value("${kafka.consumer-concurrency}")
  private int consumerConcurrency;

  @Value("${kafka.topic-name-delimiter:_}")
  private String topicNameDelimiter;

  @Value("${kafka.dlq-suffix:DLQ}")
  private String deadLetterQueueSuffix;

  @Autowired
  protected Environment env;

  @Autowired
  private KafkaConsumerConfiguration defaultConsumerPropsConfiguration;

  @Autowired
  private KafkaProducerConfiguration kafkaProducerConfiguration;

  @Autowired
  private GenericApplicationContext context;

  @Getter
  private KafkaConsumerConfigurationProperties config;

  private KafkaTemplate<K, V> dlqSender;

  @PostConstruct
  public void init() {

    if (!KAFKA_TOPIC_NAME_DELIMITERS.contains(topicNameDelimiter)) {
      throw new IllegalStateException(String.format(
          "Invalid kafka topic name delimiter '%s'. Only '-', '_', '.' and '' are acceptable values.",
          topicNameDelimiter));
    }

    config = generateKafkaConsumerConfigurationProperties(getTopicName());

    if (config.isConsumerEnabled() && config.isDlqPublishEnabled()) {
      DefaultKafkaProducerFactory<K, V> producerFactory =
          new DefaultKafkaProducerFactory<>(kafkaProducerConfiguration.getProducer());
      dlqSender = new KafkaTemplate<>(producerFactory);
      getLogger().info("DLQ sender initialised for topic: {}", getTopicName());
    } else {
      getLogger().warn("DLQ publish disabled for topic: {}", getTopicName());
    }

  }

  @SuppressWarnings("unchecked")
  @EventListener(ApplicationReadyEvent.class)
  public void consume() {
    if (config.isConsumerEnabled()) {
      final String consumerTopicName = generateTopicName();

      Map<String, Object> consumerProps =
          new HashMap<>(defaultConsumerPropsConfiguration.getConsumer());
      configureConsumerProperties(consumerProps, config.getGroupId());

      DefaultKafkaConsumerFactory<K, V> kafkaConsumerFactory =
          new DefaultKafkaConsumerFactory<>(consumerProps);

      SeekToCurrentErrorHandler seekToCurrentErrorHandler;

      FixedBackOff fixedBackOff = new FixedBackOff(fixedBackOffInterval, fixedBackOffMaxAttempts);

      if (config.isDlqPublishEnabled()) {
        DeadLetterPublishingRecoverer recoverer =
            new DeadLetterPublishingRecoverer(dlqSender, (r, e) -> {
              getLogger().error("Retries exhausted for consumer topic: {}, offset: {}, key: {}. Sending to DLQ",
                  r.topic(), r.topic(), r.key());
              return new TopicPartition(generateDLQTopicName(), r.partition());
            });

        seekToCurrentErrorHandler = new SeekToCurrentErrorHandler(recoverer, fixedBackOff);
      } else {
        seekToCurrentErrorHandler = new SeekToCurrentErrorHandler(fixedBackOff);
      }
      ContainerProperties containerProperties = new ContainerProperties(consumerTopicName);

      if (kafkaConsumerFactory.isAutoCommit()) {
        MessageListener<K, V> messageListener = new MessageListener<K, V>() {
          @Override
          public void onMessage(ConsumerRecord<K, V> data) {
            long s = System.currentTimeMillis();
            processMessage(data);
            logResponse(data, true);
          }
        };

        containerProperties.setMessageListener(messageListener);
      } else {
        AcknowledgingMessageListener<K, V> messageListener =
            new AcknowledgingMessageListener<K, V>() {
              @Override
              public void onMessage(ConsumerRecord<K, V> data, Acknowledgment acknowledgment) {
                long s = System.currentTimeMillis();
                processMessage(data);
                acknowledge(data, acknowledgment);
                logResponse(data, false);
              }
            };

        containerProperties.setMessageListener(messageListener);
        containerProperties.setAckMode(AckMode.MANUAL_IMMEDIATE);
        seekToCurrentErrorHandler.setCommitRecovered(true);
      }
      context.registerBean(consumerTopicName + HYPHEN + CONSUMER,
          ConcurrentMessageListenerContainer.class,
          () -> new ConcurrentMessageListenerContainer<>(kafkaConsumerFactory,
              containerProperties));
      ConcurrentMessageListenerContainer<K, V> container =
          (ConcurrentMessageListenerContainer<K, V>) context
              .getBean(consumerTopicName + HYPHEN + CONSUMER);
      container.setErrorHandler(seekToCurrentErrorHandler);
      container.setConcurrency(consumerConcurrency);
      container.start();
    }
  }

  private void acknowledge(ConsumerRecord<K, V> receivedRecord, Acknowledgment acknowledgment) {
    acknowledgment.acknowledge();
  }

  protected void configureConsumerProperties(Map<String, Object> consumerProps, String groupId) {
    consumerProps.put(JsonDeserializer.KEY_DEFAULT_TYPE, getKeyClass());
    consumerProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, getValueClass());
    if (groupId != null) {
      consumerProps.put(GROUP_ID_CONFIG, groupId);
    }
  }

  protected String generateTopicName() {
    String topicName = HYPHEN.equals(topicNameDelimiter) ? getTopicName()
        : getTopicName().replace(HYPHEN, topicNameDelimiter);
    String generatedTopicName = topicName;
    if (!customerName.equals("") && !region.equals("")) {
      generatedTopicName = String.join(topicNameDelimiter, customerName, topicName, region);
    }
    return env.getProperty(OVERRIDE_KAFKA_TOPIC_NAME_PROPERTY_PREFIX + generatedTopicName,
        generatedTopicName);
  }

  protected String generateDLQTopicName() {
    String topicName = HYPHEN.equals(topicNameDelimiter) ? getTopicName()
        : getTopicName().replace(HYPHEN, topicNameDelimiter);
    String dlqTopicName;
    if (!customerName.equals("") && !region.equals("")) {
      dlqTopicName = String
          .join(topicNameDelimiter, customerName, topicName, region, deadLetterQueueSuffix);
    } else {
      dlqTopicName = String.join(topicNameDelimiter, topicName, deadLetterQueueSuffix);
    }
    return env.getProperty(OVERRIDE_KAFKA_TOPIC_NAME_PROPERTY_PREFIX + dlqTopicName, dlqTopicName);
  }

  // To collect all topic specific properties.
  protected KafkaConsumerConfigurationProperties generateKafkaConsumerConfigurationProperties(
      String topicName) {
    return KafkaConsumerConfigurationProperties.builder()
        .consumerEnabled(env.getProperty(
            String.format(CONSUMER_ENABLED_PROPERTY_OVERRIDE, topicName), Boolean.class, true)
            && kafkaConsumerEnabled)
        .dlqPublishEnabled(
            env.getProperty(String.format(DLQ_PUBLISH_ENABLED_PROPERTY_OVERRIDE, topicName),
                Boolean.class, deadLetterQueuePublishEnabled))
        .groupId(env.getProperty(OVERRIDE_KAFKA_TOPIC_GROUP_ID_PROPERTY_PREFIX + topicName))

        .build();
  }

  protected abstract void processMessage(ConsumerRecord<K, V> receivedRecord);

  protected abstract Logger getLogger();

  protected abstract String getTopicName();

  protected abstract void logResponse(ConsumerRecord<K, V> receivedRecord, boolean isAutoCommit);

  protected abstract Class<K> getKeyClass();

  protected abstract Class<V> getValueClass();

}
