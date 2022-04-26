package com.jcp.commit.dto.request;

import com.jcp.commit.dto.audit.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Slf4j
public class CartLines implements Serializable {

    private String lineId;
    private String productId;
    private String uom;
    private int quantity;
    private String fulfillmentService;
    private String fulfillmentType;
    private Address shippingAddress;
    private String keepTogetherId;
    private String sourcingConstraint;
    private String locationId;
    private String locationType;
    private boolean considerTransfer;
    private String cartLineType;
    private Double linePriceTotal;
    private Double sellingPrice;
    private ZonedDateTime requestedDeliveryDate;
    private String requestedDeliveryDateConstraint;
    private boolean considerGlobalInventory;
    private SourcingLocations sourcingLocations;
    private LineClassifications lineClassifications;



}
