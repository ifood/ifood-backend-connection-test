package com.ifood.ifoodclient.infrastructure;

import com.ifood.ifoodclient.domain.Restaurant;
import com.ifood.ifoodclient.error.ApiException;
import com.ifood.ifoodclient.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CacheWarmupBean {

    private final CacheBean cacheBean;
    private final RestaurantRepository restaurantRepository;

    @PostConstruct
    private void populateCache(){

        final Optional<Restaurant> firstByLoggedInFalse = this.getFirstNotLoggedIn();

        firstByLoggedInFalse
                .map(this::logIn)
                .orElseThrow(() -> ApiException.builder()
                .code(ApiException.INTERNAL_ERROR)
                .message("Error on restaurant CacheWarmup process...")
                .build());
    }

    private Optional<Restaurant> getFirstNotLoggedIn(){
        return restaurantRepository.findDistinctFirstByLoggedInFalse();
    }

    /**
     * Ensure that clients always log in with distinct restaurants.
     *
     * @return logged restaurant
     */
    private synchronized Restaurant logIn(Restaurant restaurant){
        cacheBean.setCacheKey(restaurant.getCode());
        cacheBean.updateLoggedRestaurant(restaurant);
        return restaurant;
    }
}
