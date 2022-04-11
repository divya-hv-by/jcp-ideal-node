package com.jcp.commit.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Slf4j
public class IdealNodeRequestDto implements Serializable {

  private static final long serialVersionUID = -7202494042702820905L;

  private String cartId;
  private String orgId;
  private String sellingChannel;
  private String cartType;
  private Double cartPriceTotal;
  private String customerType;
  private String transactionType;
  private String sourcingConstraints;
  private AlgorithmConstraints algorithmConstraints;
  private String optimizationRuleId;
  private CartClassifications cartClassifications;
  private List<CartLines> cartLines;


}

