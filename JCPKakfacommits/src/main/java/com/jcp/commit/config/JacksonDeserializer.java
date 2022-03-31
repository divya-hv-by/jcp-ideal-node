package com.jcp.commit.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.kafka.support.serializer.JsonDeserializer;

// https://stackoverflow.com/questions/52068297/springboot-kafka-java-8-time-serialization
public class JacksonDeserializer<T> extends JsonDeserializer<T> {

  public JacksonDeserializer() {
    super(new ObjectMapper());
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
  }
}
