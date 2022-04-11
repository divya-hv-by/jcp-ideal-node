package com.jcp.commit.dto.response;

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
public class IdealNodeAuditDetails implements Serializable {

    private Map<String, Map<String, Service>> line;

}
