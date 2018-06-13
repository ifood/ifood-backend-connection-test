package com.ifood.ifoodclient.infrastructure;

import com.ifood.ifoodclient.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
@EnableCaching
@EnableScheduling
@EnableMongoAuditing
@RequiredArgsConstructor
public class ConfigClient {

    private final RestaurantRepository restaurantRepository;

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
    public TaskScheduler taskScheduler() {
        return new ConcurrentTaskScheduler();
    }

    @Bean
    public Executor taskExecutor() {
        return new SimpleAsyncTaskExecutor();
    }
}
