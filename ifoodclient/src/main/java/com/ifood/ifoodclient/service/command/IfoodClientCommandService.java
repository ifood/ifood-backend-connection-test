package com.ifood.ifoodclient.service.command;

import com.ifood.ifoodclient.domain.Restaurant;
import com.ifood.ifoodclient.error.ApiException;
import com.ifood.ifoodclient.service.query.IRestaurantQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class IfoodClientCommandService implements IifoodClientCommandService {

    private final MQTTCommandService mqttCommandService;
    private final IRestaurantQueryService restaurantQueryService;
    private final IRestaurantCommandService restaurantCommandService;

    private static final String PAYLOAD_BASE_TEXT = "Sending keep-alive signal";

    @Override
    @Scheduled(cron = "0 0/2 * 1/1 * ?")
    public void sendKeepAliveSignal() {
//        try{
//            restaurantQueryService.findAll()
//                    .stream()
//                    .forEach(restaurant -> {
//                        if (restaurant.isSendKeepAlive()){
//                            mqttCommandService.sendKeepAlive(
//                                    String.format(PAYLOAD_BASE_TEXT.concat(" for %s"), restaurant.getCode()),
//                                    restaurant.getCode());
//                            restaurantCommandService.patch(restaurant, Restaurant.builder().build());
//                            restaurantCommandService.insertRestaurantStatusLog(restaurant);
//                        }
//                    });
//        }catch (ApiException ex){
//            log.error("Error ".concat(PAYLOAD_BASE_TEXT));
//        }
    }
}
