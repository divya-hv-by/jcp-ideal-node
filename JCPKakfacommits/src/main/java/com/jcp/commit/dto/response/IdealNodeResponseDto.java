package com.jcp.commit.dto.response;

import com.jcp.commit.dto.audit.AuditDetails;
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
public class IdealNodeResponseDto implements Serializable {

    private String cartId;
    private Double fulfillmentScore;
    private List<ResponseShipments> shipments;
    private Map<String, Map<String, Service>> auditDetails;
    private Double idealNodeFulfillmentScore;
    private List<ResponseShipments> idealNodeShipments;
    private Map<String, Map<String, Service>> idealNodeAuditDetails;

}
