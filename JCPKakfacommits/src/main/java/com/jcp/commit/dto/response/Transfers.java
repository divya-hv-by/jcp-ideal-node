package com.jcp.commit.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Slf4j
public class Transfers implements Serializable {

    private String locationId;
    private String locationType;
    private String lineId;
    private int quantity;
    private Double transferScore;
    private ShipDate transferDepartureDate;
    private ShipDate transferArrivalDate;
    private String fulfillmentType;


}
