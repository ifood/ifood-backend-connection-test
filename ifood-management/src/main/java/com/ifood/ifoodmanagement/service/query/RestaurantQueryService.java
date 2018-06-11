package com.ifood.ifoodmanagement.service.query;

import com.ifood.ifoodmanagement.domain.ClientKeepAliveLog;
import com.ifood.ifoodmanagement.domain.Restaurant;
import com.ifood.ifoodmanagement.error.ApiNotFoundException;
import com.ifood.ifoodmanagement.repository.ClientKeepAliveRepository;
import com.ifood.ifoodmanagement.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.ifood.ifoodmanagement.util.IfoodUtil.isRestaurantOnline;

@Service
@RequiredArgsConstructor
public class RestaurantQueryService implements IRestaurantQueryService {

    private final RestaurantRepository restaurantRepository;
    private final ClientKeepAliveRepository clientKeepAliveRepository;

    @Override
    public Optional<Restaurant> findByCode(String code) {
        return Optional.of(restaurantRepository.findByCode(code))
                .orElseThrow(() -> ApiNotFoundException.builder()
                    .code(ApiNotFoundException.VALIDATION_ERROR)
                    .message(String.format("Could not find restaurant with code", code))
                    .build());
    }

    @Override
    public List<ClientKeepAliveLog> fetchRestaurantAvailabilityHistory(String code, String status, Interval dateInterval) {

        final Optional<DateTime> start = Optional.ofNullable(dateInterval.getStart());
        final DateTime end = Optional.ofNullable(dateInterval.getEnd()).orElse(DateTime.now());

        if (start.isPresent()){
            return clientKeepAliveRepository
                    .findByRestaurantCodeAndAvailableAndLastModifiedBetween(code, status, start.get(), end);
        }

        return clientKeepAliveRepository.findByRestaurantCodeAndAvailable(code, status);
    }

    @Override
    @Cacheable(cacheNames = {"restaurants"})
    public List<Restaurant> findAll(){
        return restaurantRepository.findAll()
                .stream()
                .map(restaurant -> {
                    restaurant.setOnline(isRestaurantOnline(restaurant.isAvailable(), restaurant.getLastModified()));
                    return restaurant;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Restaurant> fetchRestaurantsOnlineStatus(List<String> restaurantCodes) {

        return restaurantCodes.stream()
                .map(code -> {

                    Optional<Restaurant> optionalRestaurant = restaurantRepository.findByCode(code);

                    Restaurant restaurant = null;
                    if (optionalRestaurant.isPresent()){
                        restaurant = optionalRestaurant.get();
                        restaurant.setOnline(isRestaurantOnline(restaurant.isAvailable(), restaurant.getLastModified()));
                    }

                    return restaurant;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
