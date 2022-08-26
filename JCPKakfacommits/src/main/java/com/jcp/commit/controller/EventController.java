package com.jcp.commit.controller;

import com.azure.core.annotation.QueryParam;
import com.jcp.commit.dto.audit.CommitsResponseDto;
import com.jcp.commit.dto.audit.CommitsResponseKeyDto;
import com.jcp.commit.dto.request.IdealNodeRequestDto;
import com.jcp.commit.dto.request.StartEndDateRequestDto;
import com.jcp.commit.dto.response.IdealNodeResponseDto;
import com.jcp.commit.hub.EventReceiver;
import com.jcp.commit.kafka.service.KafkaEventProducer;
import com.jcp.commit.service.FileService;
import com.jcp.commit.service.IdealNodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = EventController.ENDPOINT)
public class EventController {

    static final String ENDPOINT = "/event";

    static final String SUCCESS_MESSAGE = "Success";

    @Autowired
    private KafkaEventProducer kafkaEventProducer;

    @Autowired
    private EventReceiver eventReceiver;

    @Autowired
    private IdealNodeService idealNodeService;

    @Autowired
    private FileService fileService;

    @PostMapping("/hub/post-ideal-node-to-kafka")
    public ResponseEntity<String> produceKafkaMessage(@Valid @RequestBody CommitsResponseDto commitsResponseDto) {

        kafkaEventProducer.send(CommitsResponseKeyDto.builder().build(), commitsResponseDto);

        return ResponseEntity
                .ok()
                .body(SUCCESS_MESSAGE);

    }

    @GetMapping("/hub/health")
    public ResponseEntity<String> checkHealth() {

        return ResponseEntity
                .ok()
                .body(SUCCESS_MESSAGE);

    }

    @GetMapping("/hub/read/messages")
    public ResponseEntity<String> readMessage() throws IOException {

        eventReceiver.receiveMessage();
        return ResponseEntity
                .ok()
                .body(SUCCESS_MESSAGE);

    }

    @PostMapping("/hub/process-historic-data")
    public ResponseEntity<String> processHistoricData(@Valid @RequestBody StartEndDateRequestDto startEndDateRequestDto)
            throws IOException {

        final long start = System.currentTimeMillis();

        log.info("Read file: Time taken : {} ms", System.currentTimeMillis() - start);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startTime = LocalDateTime.parse(startEndDateRequestDto.getStartTime(), formatter);
        LocalDateTime endTime = LocalDateTime.parse(startEndDateRequestDto.getEndTime(), formatter);

        idealNodeService.processHistoricData(startTime, endTime);
        return ResponseEntity
                .ok()
                .body(SUCCESS_MESSAGE);

    }

    @GetMapping("/hub/process-historic-data/{date}")
    public ResponseEntity<String> processHistoricDataByDate(@PathVariable String date, @RequestParam String sortingFlag)
            throws IOException {


        final long start = System.currentTimeMillis();

        idealNodeService.processHistoricDataByDate(LocalDate.parse(date), Boolean.valueOf(sortingFlag));
        return ResponseEntity
                .ok()
                .body(SUCCESS_MESSAGE);

    }

    @PostMapping("/hub/ideal-node")
    public ResponseEntity<IdealNodeResponseDto> getIdealNode(@Valid @RequestBody IdealNodeRequestDto idealNodeRequestDto) {

        final long start = System.currentTimeMillis();

        IdealNodeResponseDto idealNode = idealNodeService.getIdealNode(idealNodeRequestDto);
        log.info("Ideal node : Time taken : {} ms", System.currentTimeMillis() - start);
        return ResponseEntity
                .ok()
                .body(idealNode);

    }

    @PostMapping("/uploadFile")
    public ResponseEntity<Map<String,String>> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        Map<String, String> resp = new HashMap<>();
        resp.put("timestamp", Calendar.getInstance().getTime().toString());
        resp.put("status", "failed");
        if (ObjectUtils.isEmpty(file) || file.isEmpty()) {
            resp.put("message", "Invalid / Empty file");
        }
        byte[] bytes = file.getBytes();
        String fileName = file.getOriginalFilename();
        boolean saved = fileService.saveFile(fileName, bytes);
        if (saved) {
            resp.put("status","success");
        }
        return ResponseEntity.ok(resp);
    }
}
