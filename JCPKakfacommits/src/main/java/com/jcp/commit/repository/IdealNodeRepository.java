package com.jcp.commit.repository;

import com.jcp.commit.entity.HistoricDataIdealNodeEntity;
import com.jcp.commit.entity.IdealNodeEntityPK;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IdealNodeRepository extends CassandraRepository<HistoricDataIdealNodeEntity, IdealNodeEntityPK> {

    @AllowFiltering
    List<HistoricDataIdealNodeEntity> findByOrderDateBetween(LocalDateTime startTime, LocalDateTime endTime);

    List<HistoricDataIdealNodeEntity> findByKeyOrderNumber(String orderNumber);

    List<HistoricDataIdealNodeEntity> findByOrderCreatedDate(LocalDate date);

}

