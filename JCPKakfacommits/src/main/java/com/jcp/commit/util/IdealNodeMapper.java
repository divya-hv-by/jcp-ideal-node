package com.jcp.commit.util;

import com.jcp.commit.config.Properties;
import com.jcp.commit.dto.audit.*;
import com.jcp.commit.dto.request.*;
import com.jcp.commit.entity.HistoricDataIdealNodeEntity;
import com.jcp.commit.entity.IdealNodeEntityPK;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class IdealNodeMapper {

    @Autowired
    private Properties properties;

    public HistoricDataIdealNodeEntity getIdealNodeEntity(List<String> data) {
        List<String> lineRecord = Arrays.stream(data.get(0).split(",")).collect(Collectors.toList());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<String> emptyString = new ArrayList<>();
        emptyString.add("");
        emptyString.add(null);
        LocalDateTime dateTime = LocalDateTime.parse(lineRecord.get(3).substring(1, lineRecord.get(3).length() - 4), formatter);

        return HistoricDataIdealNodeEntity.builder()
                .key(IdealNodeEntityPK.builder().orderNumber(lineRecord.get(2).substring(1, lineRecord.get(2).length() - 1))
                        .lineNumber(lineRecord.get(5).substring(1, lineRecord.get(5).length() - 1)).build())
                .itemId(lineRecord.get(0).substring(1, lineRecord.get(0).length() - 1))
                .itemDescription(lineRecord.get(7))
                .quantity(lineRecord.get(6).substring(1, lineRecord.get(6).length() - 1))
                .orderDate(dateTime)
                .zipCode(emptyString.contains(lineRecord.get(17)) ? null : lineRecord.get(17).substring(1, lineRecord.get(17).length() - 1))
                .serviceCode(emptyString.contains(lineRecord.get(4)) ? null : lineRecord.get(4).substring(1, lineRecord.get(4).length() - 1))
                .price(emptyString.contains(lineRecord.get(8)) ? null : lineRecord.get(8).substring(1, lineRecord.get(8).length() - 1))
                .clearance(emptyString.contains(lineRecord.get(13)) ? null : lineRecord.get(13).substring(1, lineRecord.get(13).length() - 1))
                .stateCode(emptyString.contains(lineRecord.get(16)) ? null : lineRecord.get(16).substring(1, lineRecord.get(16).length() - 1))
                .addressClassification(emptyString.contains(lineRecord.get(18)) ? null : lineRecord.get(18).substring(1, lineRecord.get(18).length() - 1))
                .fulfillmentType(emptyString.contains(lineRecord.get(10)) ? null : lineRecord.get(10).substring(1, lineRecord.get(10).length() - 1))
                .city(emptyString.contains(lineRecord.get(15)) ? null : lineRecord.get(15).substring(1, lineRecord.get(15).length() - 1))
                .warehouseClass(emptyString.contains(lineRecord.get(9)) ? null : lineRecord.get(9).substring(1, lineRecord.get(9).length() - 1))
                .createdDateTime(LocalDateTime.now())
                .createdUserId(IdealNodeConstants.CREATED_USER)
                .modifiedUserId(IdealNodeConstants.UPDATED_USER)
                .updatedDateTime(LocalDateTime.now())
                .build();
    }

    public IdealNodeRequestDto getIdealNodeRequest(List<HistoricDataIdealNodeEntity> idealNodeOrderLineList, String orderNo) {

        List<CartLines> cartLinesList = new ArrayList<>();

        idealNodeOrderLineList.forEach(lines -> {

            CartLines cartLines = CartLines.builder().lineId(lines.getKey().getLineNumber())
                    .productId(lines.getItemId())
                    .uom(properties.getUom())
                    .quantity(Integer.parseInt(lines.getQuantity()))
                    .fulfillmentService(lines.getServiceCode())
                    .fulfillmentType(lines.getFulfillmentType())
                    .shippingAddress(Address.builder()
                            .city(lines.getCity())
                            .countryCode(properties.getCountryCOde())
                            .zipCode(lines.getZipCode())
                            .state(lines.getStateCode()).build())
                    .keepTogetherId(properties.getKeepTogetherId())
                    .sourcingConstraint(properties.getSourcingConstraint())
                    .locationId(properties.getLocationId())
                    .locationType(properties.getLocationType())
                    .considerTransfer(false)
                    .cartLineType(properties.getCartLineType())
                    .linePriceTotal(Double.valueOf(lines.getPrice().substring(1, lines.getPrice().length() - 1)))
                    .sellingPrice(Double.valueOf(lines.getPrice().substring(1, lines.getPrice().length() - 1)))
                    //.requestedDeliveryDate()
                    //.requestedDeliveryDateConstraint()
                    .considerGlobalInventory(properties.isConsiderGlobalInventory())
                    //.sourcingLocations()
                    .lineClassifications(LineClassifications.builder().addressClassifications(properties.getAddressClassification())
                            .deliveryClassifications(properties.getDeliveryClassification()).build())
                    .build();
            cartLinesList.add(cartLines);

        });

        return IdealNodeRequestDto.builder()
                .cartId(orderNo)
                .orgId(properties.getOrgId())
                .sellingChannel(properties.getSellingChannel())
                .cartType(properties.getCartType())
                .cartPriceTotal(idealNodeOrderLineList.stream()
                        .mapToDouble(lines-> Double.parseDouble(lines.getPrice() == null ? "0"
                                : lines.getPrice().substring(1, lines.getPrice().length() - 1))).sum())
                .customerType(properties.getCustomerType())
                .transactionType(properties.getTransactionType())
                .sourcingConstraints(properties.getSourcingConstraint())
                .algorithmConstraints(AlgorithmConstraints.builder().forceGreedy(properties.isForceGreedy()).build())
                .optimizationRuleId(properties.getOptimizationRuleId())
                .cartLines(cartLinesList)
                .cartClassifications(CartClassifications.builder().sourcingClassification(properties.getSourcingClassification()).build())
                .build();
    }

    public CommitsResponseDto addOrderDetailsToCommitsResponse(IdealNodeRequestDto idealNodeRequestDto,
                                                               List<HistoricDataIdealNodeEntity> idealNodeOrderLineList,
                                                               CommitsResponseDto commitsResponseDto) {
        List<OrderLines> orderLinesList = new ArrayList<>();
        idealNodeRequestDto.getCartLines().forEach(orderLines -> {

            OrderLines line = OrderLines.builder()
                    .lineId(orderLines.getLineId())
                    .itemId(ItemId.builder().itemId(orderLines.getProductId())
                            .uom(idealNodeRequestDto.getCartLines().get(0).getUom()).build())
                    .quantity(orderLines.getQuantity())
                    .keepTogetherId(orderLines.getKeepTogetherId())
                    .sourcingConstraint(orderLines.getSourcingConstraint())
                    .locationId(orderLines.getLocationId())
                    .locationType(orderLines.getLocationType())
                    .considerTransfer(orderLines.isConsiderTransfer())
                    .cartLineType(orderLines.getCartLineType())
                    .linePriceTotal(orderLines.getLinePriceTotal())
                    .sellingPrice(orderLines.getSellingPrice())
                    .requestedDeliveryDate(orderLines.getRequestedDeliveryDate())
                    .requestedDeliveryDateConstraint(orderLines.getRequestedDeliveryDateConstraint())
                    .considerGlobalInventory(orderLines.isConsiderGlobalInventory())
                    .sourcingLocations(orderLines.getSourcingLocations())
                    .lineClassifications(orderLines.getLineClassifications())
                    .build();
            orderLinesList.add(line);

        });

        // TODO : Map order detail
        OrderDetails orderDetails = OrderDetails.builder()
                .id(idealNodeRequestDto.getCartId())
                .orgId(idealNodeRequestDto.getOrgId())
                .sellingChannel(idealNodeRequestDto.getSellingChannel())
                .ignoreCapacity(Boolean.getBoolean(properties.getIgnoreCapacity()))
                .ignoreExistingDemand(properties.getIgnoreExistingDemand())
                .orderLines(orderLinesList)
                .fulfillmentService(idealNodeRequestDto.getCartLines().get(0).getFulfillmentService())
                .shippingAddress(idealNodeRequestDto.getCartLines().get(0).getShippingAddress())
                .countryCode(idealNodeRequestDto.getCartLines().get(0).getShippingAddress().getCountryCode())
                .zipCode(idealNodeOrderLineList.get(0).getZipCode())
                .optimizationRuleId(properties.getOptimizationRuleId())
                .reservationOrder(properties.isReservationOrder())
                .redecideOrder(properties.isRedecideOrder())
                .orderCreationTime(idealNodeOrderLineList.get(0).getOrderDate().atZone(ZoneId.of( "UTC")))
                .ignoreAvailability(Boolean.getBoolean(properties.getIgnoreAvailability()))
                .idealNodeOrder(Boolean.getBoolean(properties.getIdealNodeOnly()))
                .build();
        commitsResponseDto.setOrderDetails(orderDetails);
        return commitsResponseDto;
    }
}
