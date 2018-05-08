package br.com.ifood.connection.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.ignite.IgniteCache;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.ifood.connection.cache.util.CacheUtil;
import br.com.ifood.connection.controller.response.OnlineStatusResponse;
import br.com.ifood.connection.data.entity.StatusEntity;
import br.com.ifood.connection.data.entity.status.StatusType;
import br.com.ifood.connection.data.repository.StatusRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@Api(description = "Status controller", tags = { "Status" })
@RestController
@RequestMapping(value = "/status", produces = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class StatusController {

    @Qualifier(value = "restaurants-status")
    private final IgniteCache<String, StatusEntity> onlineStatusCache;

    private final StatusRepository statusRepository;

    @ApiOperation(value = "Gets the online status of the restaurants", response = OnlineStatusResponse.class)
    @GetMapping
    public OnlineStatusResponse getOnlineStatus(@RequestParam("ids") String ids) {

        String[] restaurantsIds = ids.split(",");

        return new OnlineStatusResponse(
                Arrays.stream(restaurantsIds)
                        .map(this::checkIsOnline)
                        .collect(Collectors.toList()));
    }

    private Boolean checkEntryExistsAndIsOnline(String key) {

        StatusEntity status = onlineStatusCache.get(key);

        return status != null && status.getType() == StatusType.ONLINE;
    }

    private Boolean checkIsOnline(String restaurantId) {
        Optional<StatusEntity> schedule = statusRepository
                .findSpecificSchedule(Long.valueOf(restaurantId), Instant.now());

        return checkEntryExistsAndIsOnline(CacheUtil.buildStatusCacheKey(Long.valueOf(restaurantId)))
                && !schedule.isPresent();
    }
}
