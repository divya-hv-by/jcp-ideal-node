package com.jcp.commit.util;

import com.jcp.commit.dto.audit.*;
import com.jcp.commit.dto.response.IdealNodeResponseDto;
import com.jcp.commit.dto.response.ResponseShipments;
import com.jcp.commit.entity.IdealNodeEntity;
import com.jcp.commit.entity.IdealNodeEntityPK;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ApiToAuditResponseMapper {

    public CommitsResponseDto getIdealNodeEntity(IdealNodeResponseDto idealNodeResponseDto) {

        return CommitsResponseDto.builder()
                .cartId(idealNodeResponseDto.getCartId())
                .solution(getSolution(idealNodeResponseDto.getShipments()))
                .idealSolution(getSolution(idealNodeResponseDto.getIdealNodeShipments()))
                .fulfillmentScore(idealNodeResponseDto.getFulfillmentScore())
                .idealNodeFulfillmentScore(idealNodeResponseDto.getIdealNodeFulfillmentScore())
                .auditDetails(idealNodeResponseDto.getAuditDetails())
                .idealNodeAuditDetails(idealNodeResponseDto.getIdealNodeAuditDetails())
                .build();

    }

    private List<Solution> getSolution(List<ResponseShipments> shipmentsRequest) {

        List<Solution> solutionList = new ArrayList<>();

        shipmentsRequest.forEach(apiShipment -> {

            Map<String, Quantity> commitResponseLinesList = new HashMap<>();

            apiShipment.getCartItems().forEach((line, qty) -> {

                Quantity quantity = Quantity.builder()
                        .quantity(qty.getFulfillQuantity())
                        .requestQuantity(qty.getRequestQuantity()).build();
                commitResponseLinesList.put(line, quantity);

            });

            ShipAndDeliveryDates shipAndDeliveryDates = ShipAndDeliveryDates.builder()
                    .reservationDate(apiShipment.getReservationDate())
                    .minOrderCutOffDate(apiShipment.getOrderCutOffDate().getMin())
                    .maxOrderCutOffDate(apiShipment.getOrderCutOffDate().getMax())
                    .minShipDate(apiShipment.getShipDate().getMin())
                    .maxShipDate(apiShipment.getShipDate().getMax())
                    .minDeliveryDate(apiShipment.getDeliveryDate().getMin())
                    .maxDeliveryDate(apiShipment.getDeliveryDate().getMax())
                    .minReleaseDate(apiShipment.getReleaseDate().getMin())
                    .maxReleaseDate(apiShipment.getReleaseDate().getMax())
                    .minCapacityConsumedDate(apiShipment.getCapacityDate() == null ? null : apiShipment.getCapacityDate().getMin())
                    .maxCapacityConsumedDate(apiShipment.getCapacityDate() == null ? null : apiShipment.getCapacityDate().getMax())
                    .deliveryThresholdBreach(apiShipment.isDeliveryThresholdBreach())
                    .eventDetails(apiShipment.getEventDetails())
                    .build();

            Shipments shipments = Shipments.builder()
                    .lines(Lines.builder().lines(commitResponseLinesList).build().getLines())
                    .score(apiShipment.getFulfillmentScore())
                    .shipmentId(apiShipment.getShipmentId())
                    .shipAndDeliveryDates(shipAndDeliveryDates)
                    .build();

            Solution solution = Solution.builder()
                    .locationId(apiShipment.getLocationId())
                    .locationType(apiShipment.getLocationType())
                    .shipments(Collections.singletonList(shipments))
                    .build();
            solutionList.add(solution);

        });

        return solutionList;

    }
}
