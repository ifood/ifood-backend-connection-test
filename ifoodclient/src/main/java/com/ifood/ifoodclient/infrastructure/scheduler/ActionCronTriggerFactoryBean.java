package com.ifood.ifoodclient.infrastructure.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.stereotype.Component;

import java.text.ParseException;

@Component
public class ActionCronTriggerFactoryBean extends CronTriggerFactoryBean {

    @Value("${scheduling.keepAliveCron}")
    private String pattern;

    @Autowired
    private ScheduledActionRunnerJobDetailFactory jobDetailFactory;

    @Override
    public void afterPropertiesSet() throws ParseException {
        setCronExpression(pattern);
        setJobDetail(jobDetailFactory.getObject());
        super.afterPropertiesSet();
    }
}
