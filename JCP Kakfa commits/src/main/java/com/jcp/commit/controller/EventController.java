package com.jcp.commit.controller;

import com.jcp.commit.kafka.service.AdminOrderCaptureRequestProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = EventController.ENDPOINT)
public class EventController {

    static final String ENDPOINT = "/event";

/*    private final ProducerService producerService;

    public EventController(ProducerService producerService) {
        this.producerService = producerService;
    }*/

    @Autowired
    private AdminOrderCaptureRequestProducer adminOrderCaptureRequestProducer;
  @GetMapping("/hub/{message}")
  public ResponseEntity<String> orderModification(@PathVariable  String message) {

       // producerService.sendMessage(message);

      adminOrderCaptureRequestProducer.send("1", message);

        return ResponseEntity
            .ok()
            .body("Success");

  }
}
