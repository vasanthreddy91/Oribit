package com.techtiera.docorbit.batch.config;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.core.io.Resource;

import lombok.SneakyThrows;

public class JobCompletionNotificationListener implements JobExecutionListener {

  private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

  private Resource[] resources;

  public JobCompletionNotificationListener(Resource[] resources) {
    this.resources = resources;
  }

  @SneakyThrows
  @Override
  public void afterJob(JobExecution jobExecution) {
    if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
      log.info("JOB FINISHED !!");
      if (resources == null) {
        log.info("No resource to delete");
      }
      for (Resource r : resources) {
        File file = r.getFile();
        boolean deleted = file.delete();
        if (!deleted) {
          log.info("Could not delete file " + file.getPath());
        } else {
          log.info("File deleted: " + file.getPath());
        }
      }
    }
  }
}