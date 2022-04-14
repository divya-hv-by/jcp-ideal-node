package com.jcp.commit.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Getter
@Component
public class Properties {

  @Value("${ideal.node.uri}")
  private String idealNodeUri;

  @Value("${ideal.node.params}")
  private String idealNodeParams;

  @Value("${ignore.capacity}")
  private String ignoreCapacity;

  @Value("${ignore.existing.demand}")
  private String ignoreExistingDemand;

  @Value("${ignore.availability}")
  private String ignoreAvailability;

  @Value("${ideal.node.only}")
  private String idealNodeOnly;

  @Value("${header.value}")
  private String headerValue;

  // Request param
  @Value("${org.id}")
  private String orgId;

  @Value("${selling.channel}")
  private String sellingChannel;

  @Value("${cart.type}")
  private String cartType;

  @Value("${customer.type}")
  private String customerType;

  @Value("${sourcing.constraint}")
  private String sourcingConstraint;

  @Value("${transaction.type}")
  private String transactionType;

  @Value("${force.greedy}")
  private boolean forceGreedy;

  @Value("${optimization.rule.id}")
  private String optimizationRuleId;

  @Value("${sourcing.classification}")
  private String sourcingClassification;

  @Value("${reservation.order}")
  private boolean reservationOrder;

  @Value("${redecide.order}")
  private boolean redecideOrder;

  @Value("${consider.global.inventory}")
  private boolean considerGlobalInventory;

  @Value("${address.classification}")
  private String addressClassification;

  @Value("${delivery.classification}")
  private String deliveryClassification;

  @Value("${uom}")
  private String uom;

  @Value("${country.code}")
  private String countryCOde;

  @Value("${keep.together.id}")
  private String keepTogetherId;

  @Value("${locationId.id}")
  private String locationId;

  @Value("${location.type}")
  private String locationType;

  @Value("${cart.line.type}")
  private String cartLineType;

}
