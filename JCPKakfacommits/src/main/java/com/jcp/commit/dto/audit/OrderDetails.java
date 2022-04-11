package com.jcp.commit.dto.audit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Slf4j
public class OrderDetails implements Serializable {

    private String id;
    private String orgId;
    private String sellingChannel;
    private boolean ignoreCapacity;
    private String ignoreExistingDemand;
    private List<OrderLines> orderLines;
    private String fulfillmentService;
    private Address shippingAddress;
    private String countryCode;
    private String zipCode;
    private String optimizationRuleId;
    private boolean reservationOrder;
    private boolean redecideOrder;
    private LocalDateTime orderCreationTime;
    private boolean ignoreAvailability;
    private boolean idealNodeOrder;

}
