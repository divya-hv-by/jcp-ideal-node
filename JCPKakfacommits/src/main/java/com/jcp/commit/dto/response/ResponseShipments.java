package com.jcp.commit.dto.response;

import com.jcp.commit.dto.audit.Lines;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Slf4j
public class ResponseShipments implements Serializable {

    private String locationId;
    private String locationType;
    private Double fulfillmentScore;
    private String fulfillmentType;
    private String fulfillmentService;
    private Map<String, FulfilledRequestedQty> cartItems;
    private ShipDate shipDate;
    private ShipDate deliveryDate;
    private ShipDate orderCutOffDate;
    private ShipDate releaseDate;
    private LocalDateTime reservationDate;
    private ShipDate capacityDate;
    private List<Transfers> transfers;
    private int shipmentId;
    private boolean deliveryThresholdBreach;
    private EventDetails eventDetails;

}
