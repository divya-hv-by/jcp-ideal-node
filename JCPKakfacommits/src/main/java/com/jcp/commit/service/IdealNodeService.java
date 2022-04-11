package com.jcp.commit.service;

import com.jcp.commit.dto.audit.CommitsResponseDto;
import com.jcp.commit.dto.request.IdealNodeRequestDto;
import com.jcp.commit.dto.response.IdealNodeResponseDto;
import com.jcp.commit.entity.IdealNodeEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public interface IdealNodeService {

  void readHistoricData(String filePath) throws IOException;

  void processHistoricData(LocalDateTime startTime, LocalDateTime endTime);

  CommitsResponseDto getIdealNode(IdealNodeRequestDto idealNodeRequestDto, List<IdealNodeEntity> idealNodeOrderLineList);

}
