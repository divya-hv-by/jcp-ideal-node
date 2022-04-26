package com.jcp.commit.dto.audit;

import com.jcp.commit.dto.response.EventDetails;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.time.ZonedDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Slf4j
public class ShipAndDeliveryDates implements Serializable {

    private ZonedDateTime reservationDate;
    private ZonedDateTime minOrderCutOffDate;
    private ZonedDateTime maxOrderCutOffDate;
    private ZonedDateTime minShipDate;
    private ZonedDateTime maxShipDate;
    private ZonedDateTime minDeliveryDate;
    private ZonedDateTime maxDeliveryDate;
    private ZonedDateTime minReleaseDate;
    private ZonedDateTime maxReleaseDate;
    private ZonedDateTime minCapacityConsumedDate;
    private ZonedDateTime maxCapacityConsumedDate;
    private ZonedDateTime consumptionDropTime;
    private ZonedDateTime consumptionProcessByTime;
    private ZonedDateTime transferDates;
    private ZonedDateTime deliveryThreshold;
    private boolean deliveryThresholdBreach;
    private String event;
    private EventDetails eventDetails;

}
