package com.jcp.commit.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.time.ZonedDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Slf4j
public class EventDetails implements Serializable {

    private String name;
    private String description;
    private ZonedDateTime deliveryDate;
    private boolean upgradeService;

}
