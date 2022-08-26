package com.jcp.commit.kafka.service;


import com.azure.messaging.eventhubs.EventData;
import com.jcp.commit.dto.audit.CommitsResponseDto;
import com.jcp.commit.dto.audit.CommitsResponseKeyDto;
import com.jcp.commit.hub.EventSender;
import com.jcp.commit.kafka.AbstractKafkaConsumerImpl;
import com.jcp.commit.service.IdealNodeService;
import com.jcp.commit.util.DtoToJsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

import static com.jcp.commit.kafka.service.KafkaConstants.EVENT_HUB_TOPIC;
import static com.jcp.commit.kafka.service.KafkaConstants.ORDER_GROUPING_TOPIC;

@Slf4j
@Component
public class KafkaOrderNumberConsumer extends AbstractKafkaConsumerImpl<String, String> {

  @Autowired
  private EventSender sender;

  @Autowired
  private DtoToJsonMapper dtoToJsonMapper;

  @Autowired
  private IdealNodeService idealNodeService;

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
    return ORDER_GROUPING_TOPIC;
  }



  @Override
  public void logResponse(ConsumerRecord<String, String> shipmentUpdatesConsumerRecord, boolean isAutoCommit) {
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

    log.info("Receive shipment update, Received mesage. Key: {}, value : {} ", record.key(),
            record.value());
    idealNodeService.sendIdealNodeForOrderToEventTopic(record.value());

  }
}
