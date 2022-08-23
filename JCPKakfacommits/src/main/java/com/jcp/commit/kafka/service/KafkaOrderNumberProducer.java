package com.jcp.commit.kafka.service;


import com.jcp.commit.dto.audit.CommitsResponseDto;
import com.jcp.commit.dto.audit.CommitsResponseKeyDto;
import com.jcp.commit.kafka.AbstractKafkaProducerImpl;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import static com.jcp.commit.kafka.service.KafkaConstants.EVENT_HUB_TOPIC;
import static com.jcp.commit.kafka.service.KafkaConstants.ORDER_GROUPING_TOPIC;

@Slf4j
@Component
public class KafkaOrderNumberProducer extends AbstractKafkaProducerImpl<String, String> {

  @Override
  protected Logger getLogger() {
    return log;
  }

  @Override
  protected String getTopicName() {
    return ORDER_GROUPING_TOPIC;
  }

  @Override
  protected void logResponse(SendResult<String, String> result) {
    try {
      log.info("Published to topic : {} ", result.getProducerRecord().topic());
    } catch (Exception e) {
      log.error("Error while logging the topic: {} for order number: {}", result.getRecordMetadata().topic(),
              result.getProducerRecord().key());
    }
  }

}
