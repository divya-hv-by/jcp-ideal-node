package com.jcp.commit.scheduler;


import com.jcp.commit.service.IdealNodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

@Configuration
@EnableScheduling
@Slf4j
@Profile("ideal-node-scheduler")
public class IdealNodeBatchProcessSchedulerJob {

  @Autowired
  private IdealNodeService idealNodeService;

  @Scheduled(cron = "${cron.expression.ideal.node}", zone = "IST")
  public void idealNodeBatchProcess() {

    log.info("ideal node batch process job started at : {} ", new Date());
    long idealNodeStartTime = System.currentTimeMillis();
    LocalDateTime now = LocalDateTime.now();
    idealNodeService.processHistoricData(now.with(LocalTime.MIN), now.with(LocalTime.MAX));
    log.info("ideal node batch process  job ended after :  {} ms", System.currentTimeMillis() - idealNodeStartTime);
    log.info("ideal node batch process  job ended at : {} ", new Date());

  }

}
