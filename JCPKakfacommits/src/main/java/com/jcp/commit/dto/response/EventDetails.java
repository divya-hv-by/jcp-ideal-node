package com.jcp.commit.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Slf4j
public class EventDetails implements Serializable {

    private String name;
    private String description;
    private LocalDateTime deliveryDate;
    private boolean upgradeService;

}
