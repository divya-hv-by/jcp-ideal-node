package com.jcp.commit.kafka.service;


import com.azure.messaging.eventhubs.EventData;
import com.jcp.commit.event.AbstractKafkaConsumerImpl;
import com.jcp.commit.hub.Receiver;
import com.jcp.commit.hub.Sender;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class FulfillmentOrderLineConsumer extends AbstractKafkaConsumerImpl<String, String> {

  @Autowired
  private Sender sender;

  @Autowired
  private Receiver receiver;

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
    return "order_capture_request";
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
  protected void processMessage(ConsumerRecord<String, String> shipmentUpdatesConsumerRecord) {
    log.info("Receive shipment update, Recieved mesage. Key: {}", shipmentUpdatesConsumerRecord.key());

    log.info("Reading message : {} ", shipmentUpdatesConsumerRecord.value());
    try {
      List<EventData> allEvents = Arrays.asList(new EventData(shipmentUpdatesConsumerRecord.value()), new EventData(shipmentUpdatesConsumerRecord.value()));
      System.out.println("444444444-----"+allEvents.get(0));
      sender.publishEvents(allEvents);
      /*Receiver rr = new Receiver();
      rr.receiveMessage();*/
    } catch (Exception e) {
      log.error("Failed to send message to event hub: {}", e.getLocalizedMessage());
      e.printStackTrace();
    }

  }
}
