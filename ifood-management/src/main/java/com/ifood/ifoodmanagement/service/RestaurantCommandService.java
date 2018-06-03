package com.ifood.ifoodmanagement.service;

import com.ifood.ifoodmanagement.domain.Restaurant;
import com.ifood.ifoodmanagement.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RestaurantCommandService implements IRestaurantCommandService {

    private final RestaurantRepository restaurantRepository;

    @Override
    public Restaurant create(Restaurant restaurant) {
        return restaurantRepository.save(restaurant);
    }

    @Override
    public Restaurant patch(Restaurant existingRestaurant, Restaurant newRestaurant) {

        Optional.ofNullable(newRestaurant.getCode()).ifPresent(existingRestaurant::setCode);
        existingRestaurant.setLastModified(DateTime.now());

        return restaurantRepository.save(existingRestaurant);
    }
}
