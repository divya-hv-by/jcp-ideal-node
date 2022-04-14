package com.jcp.commit.util;

import com.jcp.commit.config.Properties;
import com.jcp.commit.dto.audit.*;
import com.jcp.commit.dto.request.*;
import com.jcp.commit.entity.IdealNodeEntity;
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

    public IdealNodeEntity getIdealNodeEntity(List<String> data) {
        List<String> lineRecord = Arrays.stream(data.get(0).split(",")).collect(Collectors.toList());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(lineRecord.get(3).substring(1, lineRecord.get(3).length() - 4), formatter);

        return IdealNodeEntity.builder()
                .key(IdealNodeEntityPK.builder().orderNumber(lineRecord.get(2).substring(1, lineRecord.get(2).length() - 1))
                        .lineNumber(lineRecord.get(5).substring(1, lineRecord.get(5).length() - 1)).build())
                .itemId(lineRecord.get(0).substring(1, lineRecord.get(0).length() - 1))
                .itemDescription(lineRecord.get(7))
                .quantity(lineRecord.get(6).substring(1, lineRecord.get(6).length() - 1))
                .orderDate(dateTime)
                .zipCode(lineRecord.get(17))
                .serviceCode(lineRecord.get(4))
                .price(lineRecord.get(8))
                .clearance(lineRecord.get(13))
                .stateCode(lineRecord.get(16))
                .addressClassification(lineRecord.get(18))
                .fulfillmentType(lineRecord.get(10))
                .city(lineRecord.get(15))
                .warehouseClass(lineRecord.get(9))
                .createdDateTime(LocalDateTime.now())
                .createdUserId(IdealNodeConstants.CREATED_USER)
                .modifiedUserId(IdealNodeConstants.UPDATED_USER)
                .updatedDateTime(LocalDateTime.now())
                .build();
    }

    public IdealNodeRequestDto getIdealNodeRequest(List<IdealNodeEntity> idealNodeOrderLineList, String orderNo) {

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
                                                               List<IdealNodeEntity> idealNodeOrderLineList,
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
