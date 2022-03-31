package com.jcp.commit.controller;

import com.jcp.commit.hub.EventReceiver;
import com.jcp.commit.kafka.service.KafkaEventProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping(value = EventController.ENDPOINT)
public class EventController {

    static final String ENDPOINT = "/event";

    @Autowired
    private KafkaEventProducer kafkaEventProducer;

    @Autowired
    private EventReceiver eventReceiver;

    @GetMapping("/hub/{message}")
    public ResponseEntity<String> produceKafkaMessage(@PathVariable String message) {

        kafkaEventProducer.send("1", message);

        return ResponseEntity
                .ok()
                .body("Success");

    }

    @GetMapping("/hub/health")
    public ResponseEntity<String> checkHealth() {

        return ResponseEntity
                .ok()
                .body("Success");

    }

    @GetMapping("/hub/read/messages")
    public ResponseEntity<String> readMessage() throws IOException {

        eventReceiver.receiveMessage();
        return ResponseEntity
                .ok()
                .body("Success");

    }
}
