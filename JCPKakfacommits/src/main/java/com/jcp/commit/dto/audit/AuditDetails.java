package com.jcp.commit.dto.audit;

import com.jcp.commit.dto.response.Service;
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
public class AuditDetails implements Serializable {

    private Map<String, Map<String, Service>> line;
}
