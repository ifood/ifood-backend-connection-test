package com.ifood.ifoodclient.infrastructure.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

@Component
public class ActionSchedulerFactoryBean extends SchedulerFactoryBean {

    @Autowired
    private ScheduledActionRunnerJobDetailFactory jobDetailFactory;

    @Autowired
    private ActionCronTriggerFactoryBean triggerFactory;

    @Override
    public void afterPropertiesSet() throws Exception {
        setJobDetails(jobDetailFactory.getObject());
        setTriggers(triggerFactory.getObject());
        super.afterPropertiesSet();
    }
}
