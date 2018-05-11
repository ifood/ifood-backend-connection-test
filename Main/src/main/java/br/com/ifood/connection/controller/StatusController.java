package br.com.ifood.connection.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.time.Instant;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.ignite.IgniteCache;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.ifood.connection.cache.util.CacheUtil;
import br.com.ifood.connection.controller.exception.handler.ErrorMessage;
import br.com.ifood.connection.controller.response.OnlineStatusResponse;
import br.com.ifood.connection.controller.validator.annotation.IdList;
import br.com.ifood.connection.data.entity.StatusEntity;
import br.com.ifood.connection.data.repository.StatusRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;

@Api(value = "Status controller", tags = { "Status" })
@RequestMapping(value = "/status", produces = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@RestController
@Validated
public class StatusController {

    @Qualifier("${app.cache.restaurants.status}")
    private final IgniteCache<String, StatusEntity> onlineStatusCache;

    private final StatusRepository statusRepository;

    @ApiOperation(value = "Online status of the restaurants", //
            notes = "Returns the current status [true/false] for the restaurants based on the ids passed as parameter"
                    + ".<br/> It will return true for the restaurant that has sent a keepalive on the last X minute "
                    + "(being X the timeout to consider the restaurant offline) and does not have a unavailability "
                    + "schedule for now.")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad request", response = ErrorMessage.class)
    })
    @GetMapping
    public OnlineStatusResponse getOnlineStatus(
            @ApiParam(value = "The list of restaurants ids separated by comma (',')", required = true) //
            @RequestParam("ids") @IdList String ids) {

        String[] restaurantsIds = ids.split(",");

        return new OnlineStatusResponse(
                Arrays.stream(restaurantsIds)
                        .map(String::trim)
                        .map(Long::parseLong)
                        .map(this::checkIsOnline)
                        .collect(Collectors.toList()));
    }

    /**
     * Checks only for the existence of the restaurant key in the cache.<br/>
     * Obs.: the cache takes care of deleting the key when the offline threshold defined in the application properties
     * is achieved.
     * 
     * @param restaurantId
     * @return
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
