/*
package com.jcp.commit.kafka.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Service
public final class ProducerService {
    private static final Logger logger = LoggerFactory.getLogger(ProducerService.class);

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final String TOPIC = "order_capture_integration";

    public ProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    public void sendMessage(String message) {

        logger.info("In event publisher,  sending message to topic : {}", TOPIC);

        ListenableFuture<SendResult<String, String>> future = this.kafkaTemplate.send(TOPIC, message);
        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
            @Override
            public void onFailure(Throwable ex) {
                logger.info("In event publisher,  failed to send message to topic : {}, {}", TOPIC, ex.getLocalizedMessage());
            }

            @Override
            public void onSuccess(SendResult<String, String> result) {
                logger.info("In event publisher,  successfully published message to topic : {}", TOPIC);
            }
        });
    }
}*/
