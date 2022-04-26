package com.jcp.commit.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;

@Slf4j
@Component
public class RestTemplateRequestResponseLoggingInterceptor implements ClientHttpRequestInterceptor {
	
	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
			throws IOException {
		logRequest(request, body);
		// TODO: Remove stopwatch later.
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		ClientHttpResponse response = execution.execute(request, body);
		logResponse(response);
		stopWatch.stop();
		log.info("Execution time of rest call {}  ms", stopWatch.getTotalTimeMillis());
		return response;
	}

	private void logRequest(HttpRequest request, byte[] body) throws IOException {
		log.info("Rest call request URI: {} method: {} Request body: {}", request.getURI(), request.getMethod(),
				new String(body, "UTF-8"));
	}

	private void logResponse(ClientHttpResponse response) throws IOException {
		log.info("Rest call response Status code: {} Response body: {}", response.getStatusCode(),
				StreamUtils.copyToString(response.getBody(), Charset.defaultCharset()));
	}
}
