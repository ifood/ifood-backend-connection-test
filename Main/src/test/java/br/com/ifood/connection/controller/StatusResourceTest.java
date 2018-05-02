package br.com.ifood.connection.controller;

import static br.com.ifood.connection.data.entity.status.StatusType.ONLINE;

import br.com.ifood.connection.cache.util.CacheUtil;
import br.com.ifood.connection.controller.response.OnlineStatusResponse;
import br.com.ifood.connection.data.entity.StatusEntity;
import br.com.ifood.connection.data.repository.StatusRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.apache.ignite.IgniteCache;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

@RunWith(SpringRunner.class)
public class StatusResourceTest {

    @TestConfiguration
    static class StatusResourceTestConfiguration {

        @Bean
        public StatusResource employeeService() {
            return new StatusResource(cache, statusRepository);
        }

    }

    @MockBean
    private static IgniteCache<String, StatusEntity> cache;

    @MockBean
    private static StatusRepository statusRepository;

    @Autowired
    private StatusResource statusResource;

    @Before
    public void setup() {
        StatusEntity statusOnline = StatusEntity.builder().type(ONLINE).build();

        Mockito.when(cache.get(CacheUtil.buildStatusCacheKey(1L))).thenReturn(statusOnline);
        Mockito.when(cache.get(CacheUtil.buildStatusCacheKey(2L))).thenReturn(null);
        Mockito.when(cache.get(CacheUtil.buildStatusCacheKey(3L))).thenReturn(statusOnline);
        Mockito.when(cache.get(CacheUtil.buildStatusCacheKey(4L))).thenReturn(null);
        Mockito.when(cache.get(CacheUtil.buildStatusCacheKey(5L))).thenReturn(statusOnline);

        Mockito.when(statusRepository.findSpecificSchedule(Mockito.eq(1L), Mockito.any()))
            .thenReturn
                (Optional.of(statusOnline));
        Mockito.when(statusRepository.findSpecificSchedule(Mockito.eq(3L), Mockito.any()))
            .thenReturn
                (Optional.of(statusOnline));
        Mockito.when(statusRepository.findSpecificSchedule(Mockito.eq(5L), Mockito.any()))
            .thenReturn
                (Optional.of(statusOnline));
    }

    @Test
    public void onlineStatus() {
        List<Boolean> expected = Arrays.asList(true, false, true, false, true);

        String paramIds = "1,2,3,4,5";

        OnlineStatusResponse onlineStatus = statusResource.getOnlineStatus(paramIds);

        List<Boolean> status = onlineStatus.getStatus();

        Assert.isTrue(status.size() == 5, "Number of elements is wrong");
        for (int i = 0; i < expected.size(); i++) {
            Assert.isTrue(expected.get(i) == status.get(i), "Result is not equals");
        }
    }
}
