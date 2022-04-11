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
public class CommitsResponseKeyDto implements Serializable {

  private static final long serialVersionUID = -7202494042702820905L;

  private String correlationId;
  private String orderId;
  private String fulfillmentType;
  private String fulfillmentService;
  private LocalDateTime timestamp;
  private String auditDataType;
  private List<String> lines;

}

