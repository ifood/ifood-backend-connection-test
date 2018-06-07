package com.ifood.ifoodclient.service.command;

import com.ifood.ifoodclient.domain.Restaurant;
import com.ifood.ifoodclient.domain.ClientKeepAliveLog;
import com.ifood.ifoodclient.repository.RestaurantRepository;
import com.ifood.ifoodclient.repository.RestaurantStatusLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.ifood.ifoodclient.util.IfoodUtil.isRestaurantOnline;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestaurantCommandService implements IRestaurantCommandService {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantStatusLogRepository restaurantStatusLogRepository;

    @Override
    public Restaurant patch(Restaurant existingRestaurant, Restaurant newRestaurant) {

        Optional.ofNullable(newRestaurant.getName()).ifPresent(existingRestaurant::setName);
        Optional.ofNullable(newRestaurant.isAvailable()).ifPresent(existingRestaurant::setAvailable);
        Optional.ofNullable(newRestaurant.isSendKeepAlive()).ifPresent(existingRestaurant::setSendKeepAlive);
        existingRestaurant.setLastModified(DateTime.now());

        return restaurantRepository.save(existingRestaurant);
    }

    @Override
    public void insertRestaurantStatusLog(Restaurant restaurant){
        log.info("Inserting keep alive log for restaurant [" + restaurant.getCode() + "]");
        restaurantStatusLogRepository.save(
            ClientKeepAliveLog.builder()
                .restaurantCode(restaurant.getCode())
                .available(restaurant.isAvailable())
                .online(isRestaurantOnline(restaurant))
                .build()
        );
    }
}
