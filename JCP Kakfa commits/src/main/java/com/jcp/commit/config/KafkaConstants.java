package com.jcp.commit.config;

import java.util.Arrays;
import java.util.List;

public class KafkaConstants {

  private KafkaConstants() {
  }
  
  public static final List<String> KAFKA_TOPIC_NAME_DELIMITERS = Arrays.asList("", "-", "_", ".");

  public static final String PRODUCER_ENABLED_PROPERTY_OVERRIDE =
      "kafka.producer.override.%s.enabled";

  public static final String HYPHEN = "-";

  public static final String OVERRIDE_KAFKA_TOPIC_NAME_PROPERTY_PREFIX = "override.topic-name.";
  public static final String OVERRIDE_KAFKA_TOPIC_GROUP_ID_PROPERTY_PREFIX = "override.group-id.";
  
  public static final String GROUP_ID_CONFIG = "group.id";
  public static final String CONSUMER = "consumer";
  
  // Application properties names
  public static final String DLQ_PUBLISH_ENABLED_PROPERTY_OVERRIDE = "kafka.consumer.override.%s.dlq-publish.enabled";
  public static final String CONSUMER_ENABLED_PROPERTY_OVERRIDE = "kafka.consumer.override.%s.enabled";
  
}