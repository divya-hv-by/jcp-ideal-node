package com.jcp.commit.dto.audit;

import com.jcp.commit.dto.response.IdealNodeAuditDetails;
import com.jcp.commit.dto.response.Service;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Slf4j
public class CommitsResponseDto implements Serializable {

  private static final long serialVersionUID = -7202494042702820905L;

  private String cartId;
  private OrderDetails orderDetails;
  private Double fulfillmentScore;
  private Double idealNodeFulfillmentScore;
  private List<Solution> solution;
  private List<Solution> idealSolution;
  private Map<String, Map<String, Service>> auditDetails;
  private Map<String, Map<String, Service>> idealNodeAuditDetails;

}

