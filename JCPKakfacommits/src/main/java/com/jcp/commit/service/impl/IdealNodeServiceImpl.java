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
import com.jcp.commit.kafka.service.KafkaOrderNumberProducer;
import com.jcp.commit.repository.IdealNodeRepository;
import com.jcp.commit.service.IdealNodeService;
import com.jcp.commit.util.ApiToAuditResponseMapper;
import com.jcp.commit.util.IdealNodeMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.reducing;

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
    private KafkaOrderNumberProducer kafkaOrderNumberProducer;

    @Autowired
    private Properties properties;


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

    @Override
    public void processHistoricDataByDate(LocalDate date, Boolean sortingFlag) {
        List<HistoricDataIdealNodeEntity> byOrderCreatedDateList = idealNodeRepository.findByOrderCreatedDate(date);

        if(!sortingFlag){

            List<HistoricDataIdealNodeEntity> sortedOrderCreatedDateList = byOrderCreatedDateList.stream().sorted(Comparator.comparing(HistoricDataIdealNodeEntity::getOrderDate)).collect(Collectors.toList());
            List<String> sortedOrderNumbersList = sortedOrderCreatedDateList.stream().map(f->f.getKey().getOrderNumber()).collect(Collectors.toList());
            Set<String> sortedOrderNumbersSet = new TreeSet<>();

            sortedOrderNumbersList.stream().forEach(orderNo ->{
                    if(!sortedOrderNumbersSet.contains(orderNo)){
                    kafkaOrderNumberProducer.send(orderNo,orderNo);
                    sortedOrderNumbersSet.add(orderNo);}
            });

        }else{
        Map<String, List<HistoricDataIdealNodeEntity>> idealNodeGroupedByItem =
                byOrderCreatedDateList.stream().collect(groupingBy(HistoricDataIdealNodeEntity::getItemId));
        Map<String, Integer> itemQtyMap = new HashMap<>();

        idealNodeGroupedByItem.forEach((item, itemIdealNode) -> {
            itemQtyMap.put(item, itemIdealNode.stream().mapToInt(line -> Integer.parseInt(line.getQuantity())).sum());
        });
                    Map<String, Integer> sortItemByCount = itemQtyMap.entrySet()
                .stream()
                .sorted((Map.Entry.<String, Integer>comparingByValue().reversed()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

                    System.out.println(sortItemByCount.size());
                    List<String> orderNumberList = new ArrayList<>();

            sortItemByCount.forEach( (itemId,count) -> idealNodeGroupedByItem.get(itemId).forEach(i-> orderNumberList.add(i.getKey().getOrderNumber())));
            orderNumberList.stream().distinct().collect(Collectors.toList()).forEach(orderNo ->kafkaOrderNumberProducer.send(orderNo,orderNo));
        }



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

    public CommitsResponseDto sendIdealNodeForOrderToEventTopic(String orderNo) {
        List<HistoricDataIdealNodeEntity> idealNodeOrderLineList = idealNodeRepository.findByKeyOrderNumber(orderNo);
        IdealNodeRequestDto idealNodeRequestDto = idealNodeMapper.getIdealNodeRequest(idealNodeOrderLineList, orderNo);

        return getIdealNodeForOrderAndSendToTopic(idealNodeRequestDto, idealNodeOrderLineList);
    }

}