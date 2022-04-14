package com.jcp.commit.adaptor.impl;


import com.jcp.commit.adaptor.CommitsAdaptor;
import com.jcp.commit.config.Properties;
import com.jcp.commit.dto.request.IdealNodeRequestDto;
import com.jcp.commit.dto.response.IdealNodeResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.Collections;

@Service
@Slf4j
public class CommitsAdaptorImpl implements CommitsAdaptor {

  @Qualifier("restTemplate")
  @Autowired
  private RestTemplate restTemplate;

  @Autowired
  private Properties properties;

  @Override
  public IdealNodeResponseDto getIdealNode(IdealNodeRequestDto idealNodeRequestDto) {
    ResponseEntity<IdealNodeResponseDto> idealNodeResponseDto = null;
    try {
      log.info("Calling ideal node for Order number : {}", idealNodeRequestDto.getCartId());

      MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
      queryParams.put("ignoreCapacity", Collections.singletonList(properties.getIgnoreCapacity()));
      queryParams.put("ignoreExistingDemand", Collections.singletonList(properties.getIgnoreExistingDemand()));
      queryParams.put("ignoreAvailability", Collections.singletonList(properties.getIgnoreAvailability()));
      queryParams.put("idealNodeOnly", Collections.singletonList(properties.getIdealNodeOnly()));

      HttpEntity<IdealNodeRequestDto> requestEntity = new HttpEntity<>(idealNodeRequestDto, getHeaders(properties.getHeaderValue()));

      UriComponents uriComponents = UriComponentsBuilder
          .fromHttpUrl(properties.getIdealNodeUri() + properties.getIdealNodeParams())
              .queryParams(queryParams)
          .build();

      idealNodeResponseDto = restTemplate.postForEntity(
              uriComponents.toUriString(), requestEntity, IdealNodeResponseDto.class);

      log.info("Received ideal node response for Order Number: {} : {} ", idealNodeRequestDto.getCartId(), idealNodeResponseDto);
    } catch (ResourceAccessException | HttpServerErrorException e) {
      log.error("Error in getting ideal response details for order number : {} Reason : {} ",
          idealNodeRequestDto.getCartId(), e.getMessage());
    }
    assert idealNodeResponseDto != null;
    return idealNodeResponseDto.getBody();
  }


  public static HttpHeaders getHeaders(String headerValue) {
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add("X-Correlation-ID", headerValue);
    httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);

    return httpHeaders;
  }

}
