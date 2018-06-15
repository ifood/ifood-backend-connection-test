package com.ifood.ifoodclient.service.command.ifood;

import com.ifood.ifoodclient.domain.Restaurant;
import com.ifood.ifoodclient.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestaurantCommandService implements IRestaurantCommandService {

    private final RestaurantRepository restaurantRepository;

    @Override
    public Restaurant patch(Restaurant existingRestaurant, Restaurant newRestaurant) {

        Optional.ofNullable(newRestaurant.getName()).ifPresent(existingRestaurant::setName);
        Optional.ofNullable(newRestaurant.isAvailable()).ifPresent(existingRestaurant::setAvailable);
        Optional.ofNullable(newRestaurant.isSendKeepAlive()).ifPresent(existingRestaurant::setSendKeepAlive);

        return restaurantRepository.save(existingRestaurant);
    }
}
