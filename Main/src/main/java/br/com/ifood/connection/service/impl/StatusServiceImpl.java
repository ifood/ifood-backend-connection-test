package br.com.ifood.connection.service.impl;

import br.com.ifood.connection.cache.util.CacheUtil;
import br.com.ifood.connection.controller.response.OnlineStatusResponse;
import br.com.ifood.connection.data.entity.StatusEntity;
import br.com.ifood.connection.data.repository.StatusRepository;
import br.com.ifood.connection.service.StatusService;
import java.time.Instant;
import java.util.Arrays;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.ignite.IgniteCache;
import org.springframework.beans.factory.annotation.Qualifier;

@RequiredArgsConstructor
public class StatusServiceImpl implements StatusService {

    @Qualifier("${app.cache.restaurants.status}")
    private final IgniteCache<String, StatusEntity> onlineStatusCache;

    private final StatusRepository statusRepository;

    @Override
    public OnlineStatusResponse getOnlineStatus(String ids) {

        String[] restaurantsIds = ids.split(",");

        return new OnlineStatusResponse(
            Arrays.stream(restaurantsIds)
                .map(String::trim)
                .map(Long::parseLong)
                .map(this::checkIsOnline)
                .collect(Collectors.toList()));
    }

    /**
     * Checks only for the existence of the restaurant key in the cache.<br/> Obs.: the cache takes
     * care of deleting the key when the offline threshold defined in the application properties is
     * achieved.
     */
    private boolean existsCacheEntryFor(Long restaurantId) {

        String key = CacheUtil.buildStatusCacheKey(restaurantId);

        return onlineStatusCache.containsKey(key);
    }

    private boolean existsUnavailableScheduleForNow(Long restaurantId) {
        return statusRepository
            .findSpecificSchedule(restaurantId, Instant.now())
            .isPresent();
    }

    private Boolean checkIsOnline(Long restaurantId) {
        return existsCacheEntryFor(restaurantId) && !existsUnavailableScheduleForNow(restaurantId);
    }
}
