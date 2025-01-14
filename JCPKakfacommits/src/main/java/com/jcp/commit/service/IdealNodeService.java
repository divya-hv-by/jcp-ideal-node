package com.jcp.commit.service;

import com.jcp.commit.dto.request.IdealNodeRequestDto;
import com.jcp.commit.dto.response.IdealNodeResponseDto;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
public interface IdealNodeService {

  void readHistoricData(String filePath) throws IOException;

  boolean isFileValid(String fileName);

  void processHistoricData(LocalDateTime startTime, LocalDateTime endTime);

  IdealNodeResponseDto getIdealNode(IdealNodeRequestDto idealNodeRequestDto);

}
