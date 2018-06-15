package com.ifood.ifoodclient.infrastructure;

import com.ifood.ifoodclient.error.ApiException;
import com.ifood.ifoodclient.infrastructure.scheduler.ScheduledActionRunner;
import com.ifood.ifoodclient.repository.RestaurantRepository;
import com.ifood.ifoodclient.service.command.ifood.IfoodClientCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.EnableAsync;

import java.io.IOException;

import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

@Slf4j
@Configuration
@EnableAsync
@RequiredArgsConstructor
public class ConfigClient {

    private final RestaurantRepository restaurantRepository;
    private final IfoodClientCommandService ifoodClientCommandService;

    @Bean
    public TerminateBean getTerminateBean() {
        return new TerminateBean(getCacheBean(), restaurantRepository);
    }

    @Bean
    public CacheBean getCacheBean() {
        return new CacheBean();
    }

    @Bean
    public CacheWarmupBean getCacheWarmupBean() {
        return new CacheWarmupBean(getCacheBean(), restaurantRepository);
    }

    @Bean
    public JobDetail jobDetail() {

        final JobDataMap newJobDataMap = new JobDataMap();
        newJobDataMap.put("clientCode", getCacheBean().getLoggedRestaurant().getCode());
        newJobDataMap.put("ifoodClientCommandService", ifoodClientCommandService);

        return JobBuilder.newJob()
                .ofType(ScheduledActionRunner.class)
                .storeDurably()
                .withIdentity(JobKey.jobKey("Qrtz_Job_Detail"))
                .withDescription("SendKeepAliveJobDetail")
                .usingJobData(newJobDataMap)
                .build();
    }

    @Bean
    public Trigger trigger() {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail())
                .withIdentity(TriggerKey.triggerKey("Qrtz_Trigger"))
                .withDescription("SendKeepAliveJobTrigger")
                .withSchedule(simpleSchedule().repeatForever().withIntervalInSeconds(30))
//                .withSchedule(simpleSchedule().repeatForever().withIntervalInMinutes(1))
                .build();
    }

    @Bean
    public Scheduler scheduler(Trigger trigger) throws SchedulerException {

        StdSchedulerFactory factory = new StdSchedulerFactory();

        try {
            factory.initialize(new ClassPathResource("quartz.properties").getInputStream());
        } catch (IOException e) {
            throw ApiException.builder()
                    .code(ApiException.INTERNAL_ERROR)
                    .message("Error retrieving settings for scheduled keep alive job.")
                    .build();
        }

        Scheduler scheduler = factory.getScheduler();
        scheduler.scheduleJob(jobDetail(), trigger);
        scheduler.start();

        return scheduler;
    }
}
