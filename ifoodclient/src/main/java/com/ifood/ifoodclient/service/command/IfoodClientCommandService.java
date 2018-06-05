package com.ifood.ifoodclient.service.command;

import com.ifood.ifoodclient.domain.Restaurant;
import com.ifood.ifoodclient.error.ApiException;
import com.ifood.ifoodclient.infrastructure.CacheBean;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class IfoodClientCommandService implements IifoodClientCommandService {

    private final MQTTCommandService mqttCommandService;
    private final IRestaurantCommandService restaurantCommandService;
    private final CacheBean cacheBean;

    private static final String PAYLOAD_BASE_TEXT = "Sending keep-alive signal";

    @Value("${cachesettings.defaultKey}")
    private String CACHE_KEY;

    @Override
    @Scheduled(cron = "0 0/2 * 1/1 * ?")
    public void sendKeepAliveSignal() {

        try{
            Restaurant restaurant = cacheBean.getRestaurant();

            if (restaurant.isSendKeepAlive()){
                mqttCommandService.sendKeepAlive(
                        String.format(PAYLOAD_BASE_TEXT.concat(" for %s"), restaurant.getCode()),
                        restaurant.getCode());
                restaurantCommandService.patch(restaurant, Restaurant.builder().build());
                restaurantCommandService.insertRestaurantStatusLog(restaurant);
            }
        }catch (ApiException ex){
            log.error("Error ".concat(PAYLOAD_BASE_TEXT));
        }
    }
}
