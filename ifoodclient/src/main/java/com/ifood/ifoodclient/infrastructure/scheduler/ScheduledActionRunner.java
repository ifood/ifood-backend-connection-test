package com.ifood.ifoodclient.infrastructure.scheduler;

import com.ifood.ifoodclient.service.command.ifood.IfoodClientCommandService;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

@Slf4j
@Component
@NoArgsConstructor
public class ScheduledActionRunner extends QuartzJobBean {

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
        final IfoodClientCommandService ifoodClientCommandService =
                (IfoodClientCommandService) jobExecutionContext.getMergedJobDataMap().get("ifoodClientCommandService");
        ifoodClientCommandService.performDefaultRestaurantScheduledOperations();
    }
}
