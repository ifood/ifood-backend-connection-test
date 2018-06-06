package com.ifood.ifoodclient.service.query;

import com.ifood.ifoodclient.domain.Restaurant;
import com.ifood.ifoodclient.error.ApiNotFoundException;
import com.ifood.ifoodclient.infrastructure.CacheBean;
import com.ifood.ifoodclient.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestaurantQueryService implements IRestaurantQueryService {

    private final RestaurantRepository restaurantRepository;
    private final CacheBean cacheBean;

    @Override
    public Optional<Restaurant> findByCode(String code) {

        return Optional.of(getFromCache(code))
                .orElse(restaurantRepository.findByCode(code));
    }

    private Optional<Restaurant> getFromCache(String code){

        final Restaurant restaurant = cacheBean.getRestaurant();

        if (!code.equalsIgnoreCase(restaurant.getCode())){
            log.error(("Error retrieving cached information for restaurant."));
            log.error(String.format("Restaurant logged in with code [%s] does not correspond to given query", code));
            throw ApiNotFoundException.builder()
                    .code(ApiNotFoundException.VALIDATION_ERROR)
                    .message(String.format("Error retrieving information for restaurant with code [%s]", code))
                    .build();
        }

        return Optional.ofNullable(restaurant);
    }
}
