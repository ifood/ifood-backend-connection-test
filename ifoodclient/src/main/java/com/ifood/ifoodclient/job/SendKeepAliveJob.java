package com.ifood.ifoodclient.job;

import com.ifood.ifoodclient.service.command.ifood.IfoodClientCommandService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SendKeepAliveJob implements Job {

    @Autowired
    private IfoodClientCommandService ifoodClientCommandService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        ifoodClientCommandService.performDefaultRestaurantScheduledOperations();
    }
}
