package com.ifood.ifoodclient.infrastructure.bootstrap;

import com.ifood.ifoodclient.domain.Restaurant;
import com.ifood.ifoodclient.infrastructure.CacheBean;
import com.ifood.ifoodclient.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClientLoginTask implements CommandLineRunner {

    private final CacheBean cacheBean;
    private final RestaurantRepository restaurantRepository;

    @Override
    public void run(String... args) throws Exception {
        Restaurant restaurant = cacheBean.getLoggedRestaurant();
        restaurant.setLoggedIn(true);
        restaurantRepository.save(restaurant);
        log.info(String.format("%s is now logged in.", restaurant.getCode()));
    }
}
