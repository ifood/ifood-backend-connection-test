package com.ifood.ifoodclient.infrastructure;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.ifood.ifoodclient.domain.Restaurant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CacheBean {

    @Value("${cachesettings.defaultKey}")
    private String CACHE_KEY;

    public Restaurant getRestaurant(){
        Cache<String, Restaurant> restaurantCache = Caffeine.newBuilder().build();
        return restaurantCache.getIfPresent(CACHE_KEY);
    }

    public void putRestaurant(Restaurant restaurant){
        Cache<String, Restaurant> restaurantCache = Caffeine.newBuilder().build();
        restaurantCache.put(CACHE_KEY, restaurant);
    }
}
