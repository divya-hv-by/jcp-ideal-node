package com.jcp.commit.kafka.service;


import com.azure.messaging.eventhubs.EventData;
import com.jcp.commit.event.AbstractKafkaConsumerImpl;
import com.jcp.commit.hub.EventSender;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

import static com.jcp.commit.kafka.service.KafkaConstants.EVENT_HUB_TOPIC;

@Slf4j
@Component
public class KafkaEventConsumer extends AbstractKafkaConsumerImpl<String, String> {

  @Autowired
  private EventSender sender;

  @Override
  protected Class<String> getKeyClass() {
    return String.class;
  }

  @Override
  protected Logger getLogger() {
    return log;
  }

  @Override
  protected String getTopicName() {
    return EVENT_HUB_TOPIC;
  }

  @Override
  public void logResponse(ConsumerRecord<String, String > shipmentUpdatesConsumerRecord, boolean isAutoCommit) {
    try {
      log.info("Consumed message from Topic: {}, {}", shipmentUpdatesConsumerRecord.topic(), shipmentUpdatesConsumerRecord.value());
    } catch (Exception e) {
      log.error("Error while logging the topic: {} ", shipmentUpdatesConsumerRecord.topic());
    }
  }


  @Override
  protected Class<String> getValueClass() {
    return String.class;
  }

  @Override
  protected void processMessage(ConsumerRecord<String, String> record) {

    log.info("Receive shipment update, Recieved mesage. Key: {}, value : {} ", record.key(),
            record.value());

    try {
      List<EventData> allEvents = Collections.singletonList(new EventData(record.value()));
      sender.publishEvents(allEvents);
    } catch (Exception e) {
      log.error("Failed to send message to event hub: {}", e.getLocalizedMessage());
      e.printStackTrace();
    }

  }
}
