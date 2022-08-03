package com.jcp.commit.service.impl;

import com.jcp.commit.adaptor.CommitsAdaptor;
import com.jcp.commit.config.Properties;
import com.jcp.commit.dto.audit.CommitsResponseDto;
import com.jcp.commit.dto.audit.CommitsResponseKeyDto;
import com.jcp.commit.dto.request.CartLines;
import com.jcp.commit.dto.request.IdealNodeRequestDto;
import com.jcp.commit.dto.response.IdealNodeResponseDto;
import com.jcp.commit.entity.HistoricDataIdealNodeEntity;
import com.jcp.commit.kafka.service.KafkaEventProducer;
import com.jcp.commit.repository.IdealNodeRepository;
import com.jcp.commit.service.IdealNodeService;
import com.jcp.commit.util.ApiToAuditResponseMapper;
import com.jcp.commit.util.IdealNodeMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
@Slf4j
public class IdealNodeServiceImpl implements IdealNodeService {

    @Autowired
    private IdealNodeRepository idealNodeRepository;

    @Autowired
    private IdealNodeMapper idealNodeMapper;

    @Autowired
    private CommitsAdaptor commitsAdaptor;

    @Autowired
    private ApiToAuditResponseMapper apiToAuditResponseMapper;

    @Autowired
    private KafkaEventProducer kafkaEventProducer;

    @Autowired
    private Properties properties;

    @Value("${ideal.node.historic.data.file.path:}")
    private String directory;

    @Async("JCPThreadPoolBean")
    public void readHistoricData(String fileName) throws IOException {

        final long startTime = System.currentTimeMillis();
        log.info("Reading file :{}", fileName);
        try {
            List<List<String>> parsedFileData = readRecords(fileName);
            log.info("Number of lines from {} : {}",fileName, parsedFileData.size());
            List<HistoricDataIdealNodeEntity> idealNodeEntityList = new ArrayList<>();
            parsedFileData.forEach(data -> {
                try {
                    HistoricDataIdealNodeEntity historicDataIdealNodeEntity = idealNodeMapper.getIdealNodeEntity(data);
                    idealNodeEntityList.add(historicDataIdealNodeEntity);
                } catch (Exception exception) {
                    log.error("Exception while parsing lines of file : {} ", exception.getStackTrace());
                }
            });

            List<HistoricDataIdealNodeEntity> entities = idealNodeRepository.saveAll(idealNodeEntityList);
            log.info("Persisted file record for day : {} ", LocalDateTime.now());
            log.info("Stored {} entries in DB", entities.size());

        } catch (Exception e) {
            log.error("Exception while reading file : {} ", e.getLocalizedMessage());
        }
        log.info("Read file: Time taken : {} ms", System.currentTimeMillis() - startTime);

    }

    public List<List<String>> readRecords(String fileName) throws FileNotFoundException {

        String filePath = directory + (directory.endsWith("/") ? "" : "/") + fileName;
        File inputF = new File(filePath);
        log.info("file path: {}", inputF.getAbsolutePath());
        InputStream inputFS = new FileInputStream(inputF);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputFS))) {
            return reader.lines()
                    .skip(1)
                    .map(line -> Arrays.asList(line.split("\n")))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Exception while reading file : {} ", e.getLocalizedMessage());
            throw new UncheckedIOException(e);
        }
    }

    public boolean isFileValid(String fileName) {
        String filePath = directory + (directory.endsWith("/") ? "" : "/") + fileName;
        return Files.exists(Paths.get(filePath));
    }

    public void processHistoricData(LocalDateTime startTime, LocalDateTime endTime) {

        List<HistoricDataIdealNodeEntity> idealNodeEntityList = idealNodeRepository.findByOrderDateBetween(startTime, endTime);

        Map<String, List<HistoricDataIdealNodeEntity>> idealNodeGroupedByOrderNumberList =
                idealNodeEntityList.stream().collect(groupingBy(idealNode -> idealNode.getKey().getOrderNumber()));

        Map<String, List<HistoricDataIdealNodeEntity>> idealNodeGroupedByItem =
                idealNodeEntityList.stream().collect(groupingBy(HistoricDataIdealNodeEntity::getItemId));
        Map<String, Integer> itemQtyMap = new HashMap<>();

        idealNodeGroupedByItem.forEach((item, itemIdealNode) -> {
            itemQtyMap.put(item, itemIdealNode.stream().mapToInt(line -> Integer.parseInt(line.getQuantity())).sum());
        });

        Map<String, Integer> sortItemByCount = itemQtyMap.entrySet()
                .stream()
                .sorted((Map.Entry.<String, Integer>comparingByValue().reversed()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        Map<String, Map<String, Integer>> idealNodeItemOrderEntityMap = new HashMap<>();

        sortItemByCount.keySet().forEach(item -> {

            Map<String, Integer> orderNumberLineMap = new HashMap<>();
            List<HistoricDataIdealNodeEntity> idealNodeEntitiesForItem = idealNodeGroupedByItem.get(item);
            Map<String, List<HistoricDataIdealNodeEntity>> idealNodeGroupedByOrderNo = idealNodeEntitiesForItem.stream()
                    .collect(groupingBy(line -> line.getKey().getOrderNumber()));

            idealNodeGroupedByOrderNo.forEach((orderNumber, idealNodeEntityByOrderNumber) -> {
                orderNumberLineMap.put(orderNumber, idealNodeEntityByOrderNumber.size());
            });

            Map<String, Integer> sortOrderByCount = orderNumberLineMap.entrySet()
                    .stream()
                    .sorted((Map.Entry.<String, Integer>comparingByValue().reversed()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

            idealNodeItemOrderEntityMap.put(item, sortOrderByCount);

        });

        sortItemByCount.forEach((item, soldQty) -> {
            idealNodeItemOrderEntityMap.get(item).forEach((orderNo, maxOrderLineCount) -> {

                List<HistoricDataIdealNodeEntity> idealNodeOrderLineList = idealNodeGroupedByOrderNumberList.get(orderNo);
                IdealNodeRequestDto idealNodeRequestDto = idealNodeMapper.getIdealNodeRequest(idealNodeOrderLineList, orderNo);

                CommitsResponseDto commitsResponseDto = getIdealNodeForOrderAndSendToTopic(idealNodeRequestDto, idealNodeOrderLineList);

            });
        });
    }

    private CommitsResponseDto getIdealNodeForOrderAndSendToTopic(IdealNodeRequestDto idealNodeRequestDto, List<HistoricDataIdealNodeEntity> idealNodeOrderLineList) {

        IdealNodeResponseDto idealNodeResponseDto = commitsAdaptor.getIdealNode(idealNodeRequestDto);
        CommitsResponseDto commitsResponseDto = apiToAuditResponseMapper.getIdealNodeEntity(idealNodeResponseDto);

        CommitsResponseDto commitsResponseDtoAfterMappingOrderDetail =
                idealNodeMapper.addOrderDetailsToCommitsResponse(idealNodeRequestDto, idealNodeOrderLineList, commitsResponseDto);

        kafkaEventProducer.send(CommitsResponseKeyDto.builder().orderId(idealNodeResponseDto.getCartId())
                        .fulfillmentService(idealNodeResponseDto.getShipments().get(0).getFulfillmentService())
                        .fulfillmentType(idealNodeResponseDto.getShipments().get(0).getFulfillmentType())
                        .lines(idealNodeRequestDto.getCartLines().stream().map(CartLines::getLineId).collect(Collectors.toList())).build(),
                commitsResponseDtoAfterMappingOrderDetail);
        return commitsResponseDtoAfterMappingOrderDetail;

    }

    public IdealNodeResponseDto getIdealNode(IdealNodeRequestDto idealNodeRequestDto) {

        return commitsAdaptor.getIdealNode(idealNodeRequestDto);

    }

}