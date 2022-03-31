package com.jcp.commit.event;


import com.jcp.commit.config.KafkaProducerConfiguration;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import javax.annotation.PostConstruct;
import java.util.concurrent.Future;

import static com.jcp.commit.config.KafkaConstants.*;

public abstract class AbstractKafkaProducerImpl<K, V> {

  @Value("${kafka.producer.enabled:true}")
  private Boolean kafkaProducerEnabled;

  @Value("${kafka.customer-name:}")
  private String customerName;

  @Value("${kafka.region:}")
  private String region;

  @Value("${kafka.topic-name-delimiter:_}")
  private String topicNameDelimiter;

  @Autowired
  private KafkaProducerConfiguration kafkaProducerConfiguration;

  @Autowired
  protected Environment env;

  private KafkaTemplate<K, V> sender;

  private Boolean producerTopicEnabled;
  private String topicName;

  @PostConstruct
  public void init() {

    if (!KAFKA_TOPIC_NAME_DELIMITERS.contains(topicNameDelimiter)) {
      throw new IllegalStateException(String.format(
          "Invalid kafka topic name delimiter '%s'. Only '-', '_', '.' and '' are acceptable values.",
          topicNameDelimiter));
    }

    producerTopicEnabled = env.getProperty(
        String.format(PRODUCER_ENABLED_PROPERTY_OVERRIDE, getTopicName()), Boolean.class, true)
        && kafkaProducerEnabled;

    if (producerTopicEnabled) {
      DefaultKafkaProducerFactory<K, V> producerFactory =
          new DefaultKafkaProducerFactory<>(kafkaProducerConfiguration.getProducer());
      this.sender = new KafkaTemplate<>(producerFactory);
      this.topicName = generateTopicName();

    } else {
      getLogger().warn("Producer disabled for topic: " + getTopicName());
    }
  }

  public V send(K key, V value) {
    if (producerTopicEnabled) {
      ProducerRecord<K, V> record = new ProducerRecord<>(topicName, key, value);
      ListenableFuture<SendResult<K, V>> future = sender.send(record);
      future.addCallback(new ListenableFutureCallback<SendResult<K, V>>() {
        @Override
        public void onFailure(Throwable ex) {
          getLogger().error("Unable to send message. Key: {}, Exception: {}", key, ex.getMessage());
        }

        @Override
        public void onSuccess(SendResult<K, V> result) {
          logResponse(result);
        }
      });

    }
    return value;
  }

  public V send(String requestTopic, K key, V value) {
      ProducerRecord<K, V> record = new ProducerRecord<>(requestTopic, key, value);
      ListenableFuture<SendResult<K, V>> future = sender.send(record);
      future.addCallback(new ListenableFutureCallback<SendResult<K, V>>() {
        @Override
        public void onFailure(Throwable ex) {
          getLogger().error("Unable to send message. Key: {} , Exception: {}", key, ex.getMessage());
        }

        @Override
        public void onSuccess(SendResult<K, V> result) {
          logResponse(result);
        }
      });
    return value;
  }

  public Future sendDlqMessage(String requestTopic, K key, V value) {
    ProducerRecord<K, V> record = new ProducerRecord<>(requestTopic, key, value);
    ListenableFuture<SendResult<K, V>> future = sender.send(record);
    return future;
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

  protected abstract Logger getLogger();

  protected abstract String getTopicName();

  protected abstract void logResponse(SendResult<K, V> result);


}
