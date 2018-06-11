package com.ifood.ifoodclient.service.command.ifood;

import com.ifood.ifoodclient.domain.Restaurant;
import com.ifood.ifoodclient.error.ApiException;
import com.ifood.ifoodclient.infrastructure.CacheBean;
import com.ifood.mqtt.ifoodmqttmanagement.service.MQTTClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class IfoodClientCommandService implements IifoodClientCommandService {

    private final MQTTClient mqttClient;
    private final IRestaurantCommandService restaurantCommandService;
    private final CacheBean cacheBean;

    private static final String PAYLOAD_BASE_TEXT = "[%s] KeepAlive-signal (%b)";

    @Override
    @Scheduled(cron = "0/30 0 0 ? * * ")    // For debugging purposes.
//    @Scheduled(cron = "0 0/1 * 1/1 * ?")  // For debugging purposes.
//    @Scheduled(cron = "0 0/2 * 1/1 * ?")
    public void performDefaultRestaurantScheduledOperations(){

        Restaurant restaurant = cacheBean.getRestaurant();

        this.sendKeepAlive(restaurant.getCode(), Boolean.toString(restaurant.isAvailable()));
        Restaurant patchedRestaurant = this.patchRestaurant(restaurant);
        this.updateCache(patchedRestaurant);
    }

    private void sendKeepAlive(String code, String isAvailable){
        try {
            mqttClient.sendKeepAlive(String.format(PAYLOAD_BASE_TEXT, new String[]{code, isAvailable}));
        } catch (ApiException ex){
            log.error(String.format("Error sending client keepAlive for restaurant [" + code + "]"));
        }
    }

    private Restaurant patchRestaurant(Restaurant restaurant){
        return restaurantCommandService.patch(restaurant, Restaurant.builder().build());
    }

    private void updateCache(Restaurant patched){
        cacheBean.putRestaurant(patched);
    }
}
