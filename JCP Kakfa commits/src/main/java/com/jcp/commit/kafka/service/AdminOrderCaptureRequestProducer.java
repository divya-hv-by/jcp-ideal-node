package com.jcp.commit.kafka.service;


import com.jcp.commit.event.AbstractKafkaProducerImpl;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AdminOrderCaptureRequestProducer extends AbstractKafkaProducerImpl<String, String> {

  @Override
  protected Logger getLogger() {
    return log;
  }

  @Override
  protected String getTopicName() {
    return "order_capture_request";
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
