package com.jcp.commit.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.io.Serializable;

@Builder
@Data
@PrimaryKeyClass
@NoArgsConstructor
@AllArgsConstructor
public class IdealNodeEntityPK implements Serializable {
  private static final long serialVersionUID = -7150333854544613835L;


  @PrimaryKeyColumn(name = "order_number", type = PrimaryKeyType.PARTITIONED)
  private String orderNumber;

  @PrimaryKeyColumn(name = "line_number", ordinal = 0)
  private String lineNumber;

}
