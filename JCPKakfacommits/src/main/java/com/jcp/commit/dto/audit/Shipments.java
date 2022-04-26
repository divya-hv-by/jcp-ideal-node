package com.jcp.commit.dto.audit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Slf4j
public class Shipments implements Serializable {

    private Map<String, Quantity> lines;
    private ShipAndDeliveryDates shipAndDeliveryDates;
    private Double score;
    private int shipmentId;

}
