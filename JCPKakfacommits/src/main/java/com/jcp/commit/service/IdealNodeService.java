package com.jcp.commit.service;

import com.jcp.commit.dto.request.IdealNodeRequestDto;
import com.jcp.commit.dto.response.IdealNodeResponseDto;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public interface IdealNodeService {

  void processHistoricData(LocalDateTime startTime, LocalDateTime endTime);

  void processHistoricDataByDate(LocalDate date);


  IdealNodeResponseDto getIdealNode(IdealNodeRequestDto idealNodeRequestDto);

}
