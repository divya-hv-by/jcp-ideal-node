package com.jcp.commit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.concurrent.Executor;

@SpringBootApplication
@EnableScheduling
@EnableAsync
@EnableTransactionManagement
public class EventApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventApplication.class, args);
	}

	@Value("${thread.poolSize:10}")
	private int threadPoolSize;

	@Value("${thread.corePoolSize:5}")
	private int corePoolSize;

	@Bean(name = "JCPThreadPoolBean")
	public Executor jcpExecutorService() {
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setMaxPoolSize(threadPoolSize);
		taskExecutor.setCorePoolSize(corePoolSize);
		taskExecutor.setThreadNamePrefix("JCP-Task");
		taskExecutor.initialize();
		return taskExecutor;
	}
}
