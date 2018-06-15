package com.ifood.ifoodclient.service.command.ifood;

import com.ifood.ifoodclient.domain.Restaurant;
import com.ifood.ifoodclient.error.ApiException;
import com.ifood.ifoodclient.infrastructure.CacheBean;
import com.ifood.ifoodclient.service.query.IRestaurantQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class IfoodClientCommandService implements IifoodClientCommandService {

    private final IfoodMqttService ifoodMqttService;
    private final IRestaurantQueryService restaurantQueryService;
    private final CacheBean cacheBean;

    private static final String PAYLOAD_BASE_TEXT = "[%s] KeepAlive-signal (%b)";

    @Override
    public void performDefaultRestaurantScheduledOperations(){

        Restaurant restaurant = cacheBean.getLoggedRestaurant();

        if (restaurant.isSendKeepAlive()){
            // Send keep-alive
            this.sendKeepAlive(restaurant.getCode(), Boolean.toString(restaurant.isAvailable()));
        }

        // Update local cache with restaurant latest information
        this.updateLocalCache(restaurant.getCode());
    }

    private void sendKeepAlive(String code, String isAvailable){
        try {
            ifoodMqttService.sendKeepAlive(String.format(PAYLOAD_BASE_TEXT, new String[]{code, isAvailable}));
        } catch (ApiException ex){
            log.error(String.format("Error sending client keepAlive for restaurant [" + code + "]"));
        }
    }

    private void updateLocalCache(@NotNull String code){
        restaurantQueryService
                .findByCode(code)
                .ifPresent(restaurant -> cacheBean.updateLoggedRestaurant(restaurant));
    }
}
