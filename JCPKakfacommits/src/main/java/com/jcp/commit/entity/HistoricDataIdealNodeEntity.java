package com.jcp.commit.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.io.Serializable;
import java.time.LocalDateTime;


@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(HistoricDataIdealNodeEntity.TABLE_NAME)
public class HistoricDataIdealNodeEntity implements Serializable {

    public static final String TABLE_NAME = "historic_data_ideal_node";

    @PrimaryKey
    private IdealNodeEntityPK key;

    @Column("item_id")
    private String itemId;

    @Column("item_description")
    private String itemDescription;

    @Column("quantity")
    private String quantity;

    @Column("order_date")
    private LocalDateTime orderDate;

    @Column("zip_code")
    private String zipCode;

    @Column("service_code")
    private String serviceCode;

    @Column("price")
    private String price;

    @Column("clearance")
    private String clearance;

    @Column("state_code")
    private String stateCode;

    @Column("city")
    private String city;

    @Column("address_classification")
    private String addressClassification;

    @Column("personalized_line_item")
    private String personalizedLineItem;

    @Column("substitute_item")
    private String substituteItem;

    @Column("fulfillment_type")
    private String fulfillmentType;

    @Column("warehouse_class")
    private String warehouseClass;

    @Column("created_date")
    private LocalDateTime createdDateTime;

    @Column("modified_date")
    private LocalDateTime updatedDateTime;

    @Column("created_user")
    private String createdUserId;

    @Column("modified_user")
    private String modifiedUserId;


}
