package com.jcp.commit.dto.audit;

import com.jcp.commit.dto.response.EventDetails;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Slf4j
public class ShipAndDeliveryDates implements Serializable {

    private LocalDateTime reservationDate;
    private LocalDateTime minOrderCutOffDate;
    private LocalDateTime maxOrderCutOffDate;
    private LocalDateTime minShipDate;
    private LocalDateTime maxShipDate;
    private LocalDateTime minDeliveryDate;
    private LocalDateTime maxDeliveryDate;
    private LocalDateTime minReleaseDate;
    private LocalDateTime maxReleaseDate;
    private LocalDateTime minCapacityConsumedDate;
    private LocalDateTime maxCapacityConsumedDate;
    private LocalDateTime consumptionDropTime;
    private LocalDateTime consumptionProcessByTime;
    private LocalDateTime transferDates;
    private LocalDateTime deliveryThreshold;
    private boolean deliveryThresholdBreach;
    private String event;
    private EventDetails eventDetails;

}
