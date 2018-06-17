package com.ifood.ifoodclient.infrastructure.scheduler;

import com.ifood.ifoodclient.service.command.ifood.IfoodClientCommandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ScheduledActionRunnerJobDetailFactory extends JobDetailFactoryBean {

    @Autowired
    private IfoodClientCommandService ifoodClientCommandService;

    @Override
    public void afterPropertiesSet() {

        setJobClass(ScheduledActionRunner.class);
        setDurability(true);

        Map<String, Object> data = new HashMap<String, Object>(){{
           put("ifoodClientCommandService", ifoodClientCommandService);
        }};

        setJobDataAsMap(data);

        super.afterPropertiesSet();
    }
}
