package com.ifood.ifoodclient.infrastructure;

import com.ifood.ifoodclient.domain.Restaurant;
import com.ifood.ifoodclient.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

@Slf4j
@Component
@RequiredArgsConstructor
public class TerminateBean {

    private final CacheBean cacheBean;
    private final RestaurantRepository restaurantRepository;

    @PreDestroy
    public void onDestroy() {
        log.info("Starting to destroy Spring Container...");
        Restaurant restaurant = cacheBean.getRestaurant();
        restaurant.setLoggedIn(false);
        restaurantRepository.save(restaurant);
    }
}
