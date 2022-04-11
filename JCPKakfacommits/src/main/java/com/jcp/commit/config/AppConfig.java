package com.jcp.commit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Configuration
public class AppConfig {

	@Bean(name = "restTemplate")
	public RestTemplate getRestTemplate(RestTemplateRequestResponseLoggingInterceptor loggingInterceptor) {
		ClientHttpRequestFactory factory;
		factory = new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()); //this is to prevent body from getting null after logging
		RestTemplate restTemplate = new RestTemplate(factory);
		restTemplate.setInterceptors(Collections.singletonList(loggingInterceptor));
		return restTemplate;
	}
	
}
