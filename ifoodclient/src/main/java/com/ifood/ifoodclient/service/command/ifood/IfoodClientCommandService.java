package com.ifood.ifoodclient.service.command.ifood;

import com.ifood.ifoodclient.domain.Restaurant;
import com.ifood.ifoodclient.error.ApiException;
import com.ifood.ifoodclient.infrastructure.CacheBean;
import com.ifood.ifoodclient.service.command.mqtt.MQTTCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class IfoodClientCommandService implements IifoodClientCommandService {

    private final MQTTCommandService mqttCommandService;
    private final IRestaurantCommandService restaurantCommandService;
    private final CacheBean cacheBean;

    private static final String PAYLOAD_BASE_TEXT = "keepAlive-%s";

    @Override
    @Scheduled(cron = "0 0/1 * 1/1 * ?")  // For debugging purposes. Uncomment Correct CRON!
//    @Scheduled(cron = "0 0/2 * 1/1 * ?")
    public void performDefaultRestaurantScheduledOperations(){

        Restaurant restaurant = cacheBean.getRestaurant();

        this.sendKeepAlive(restaurant.getCode());
        Restaurant patchedRestaurant = this.patchRestaurant(restaurant);
        //this.insertRestaurantStatusLog(patchedRestaurant);
        this.updateCache(patchedRestaurant);
    }

    private void sendKeepAlive(String code){

        try {
            mqttCommandService.sendKeepAlive(String.format(PAYLOAD_BASE_TEXT, code), code   );
        } catch (ApiException ex){
            log.error(String.format("Error sending client keepAlive for restaurant [" + code + "]"));
        }
    }

    private Restaurant patchRestaurant(Restaurant restaurant){
        return restaurantCommandService.patch(restaurant, Restaurant.builder().build());
    }

    private void insertRestaurantStatusLog(Restaurant restaurant){
        restaurantCommandService.insertRestaurantStatusLog(restaurant);
    }

    private void updateCache(Restaurant patched){
        cacheBean.putRestaurant(patched);
    }
}
