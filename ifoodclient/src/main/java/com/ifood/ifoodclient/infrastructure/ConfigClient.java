package com.ifood.ifoodclient.infrastructure;

import com.ifood.ifoodclient.error.ApiException;
import com.ifood.ifoodclient.job.SendKeepAliveJob;
import com.ifood.ifoodclient.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import java.io.IOException;
import java.util.Date;

import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

@Slf4j
@Configuration
@EnableAsync
@RequiredArgsConstructor
public class ConfigClient {

    private final RestaurantRepository restaurantRepository;
    private final ApplicationContext applicationContext;

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
        return JobBuilder.newJob()
                .ofType(SendKeepAliveJob.class)
                .storeDurably()
                .withIdentity(JobKey.jobKey("Qrtz_Job_Detail"))
                .withDescription("SendKeepAliveJobDetail.")
                .build();
    }

    @Bean
    public Trigger trigger(JobDetail job) {
        return TriggerBuilder.newTrigger()
                .forJob(job)
                .withIdentity(TriggerKey.triggerKey("Qrtz_Trigger"))
                .withDescription("SendKeepAliveJobTrigger")
                .withSchedule(simpleSchedule().repeatForever().withIntervalInSeconds(30))
                .build();
    }

    @Bean
    public Scheduler scheduler(Trigger trigger, JobDetail job) throws SchedulerException {

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
        scheduler.setJobFactory(springBeanJobFactory());
        scheduler.scheduleJob(job, trigger);
        scheduler.start();

        return scheduler;
    }

    @Bean
    public SpringBeanJobFactory springBeanJobFactory() {
        AutoWiringSpringBeanJobFactory jobFactory = new AutoWiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }
}
