package com.jcp.commit.adaptor;

import com.jcp.commit.dto.request.IdealNodeRequestDto;
import com.jcp.commit.dto.response.IdealNodeResponseDto;
import org.springframework.stereotype.Service;

@Service
public interface CommitsAdaptor {
  IdealNodeResponseDto getIdealNode(IdealNodeRequestDto idealNodeRequestDto);
}
