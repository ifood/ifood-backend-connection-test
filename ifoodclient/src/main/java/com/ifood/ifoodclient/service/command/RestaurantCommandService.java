package com.ifood.ifoodclient.service.command;

import com.ifood.ifoodclient.domain.Restaurant;
import com.ifood.ifoodclient.domain.RestaurantStatusLog;
import com.ifood.ifoodclient.repository.RestaurantRepository;
import com.ifood.ifoodclient.repository.RestaurantStatusLogRepository;
import lombok.RequiredArgsConstructor;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.ifood.ifoodclient.util.IfoodUtil.isRestaurantOnline;

@Service
@RequiredArgsConstructor
public class RestaurantCommandService implements IRestaurantCommandService {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantStatusLogRepository restaurantStatusLogRepository;

    @Override
    public Restaurant create(Restaurant restaurant) {
        return restaurantRepository.save(restaurant);
    }

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
        restaurantStatusLogRepository.save(
            RestaurantStatusLog.builder()
                .restaurantCode(restaurant.getCode())
                .available(restaurant.isAvailable())
                .online(isRestaurantOnline(restaurant))
                .build()
        );
    }
}
