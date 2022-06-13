package com.jcp.commit.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.jcp.commit.dto.audit.CommitsResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

@Slf4j
@Component
public class DtoToJsonMapper {

    public String toPojo(CommitsResponseDto targetPojo) {
        if (targetPojo == null)
            return null;
        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new ParameterNamesModule())
                .registerModule(new Jdk8Module())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .registerModule(new JavaTimeModule());
        try {
            return mapper.writeValueAsString(targetPojo);
        } catch (Exception e) {
            log.error("Error parsing string to json. Target pojo : {}", e.getLocalizedMessage());
        }
        return null;
    }
}



