/*
package com.jcp.commit.kafka.service;

import com.azure.messaging.eventhubs.EventData;
import com.jcp.commit.hub.Receiver;
import com.jcp.commit.hub.Sender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public final class ConsumerService {

    @Autowired
    private Sender eventHubSender;

    @Autowired
    private Receiver eventHubReceiver;

    private static final Logger logger = LoggerFactory.getLogger(ConsumerService.class);

    @KafkaListener(topics = "order_capture_integration", groupId = "jcp")
    public void consume(String message) {

        logger.info(String.format("Consumed message: %s", message));
        List<EventData> allEvents = Arrays.asList(new EventData(message), new EventData(message));
        System.out.println("444444444-----"+allEvents);
        eventHubSender.publishEvents(allEvents);

        logger.info("Reading message");
        try {
            eventHubReceiver.receiveMessage();
        } catch (IOException e) {
            logger.error("Failed to send message to event hub: {}", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }
}*/
