package com.ifood.ifoodclient.infrastructure;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.ifood.ifoodclient.domain.Restaurant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@Component
@RequiredArgsConstructor
public class CacheBean {

    @Value("${cachesettings.defaultKey}")
    private String CACHE_KEY;

    private Cache<String, Restaurant> restaurantCache;

    @PostConstruct
    private void initializeCache(){
        restaurantCache = Caffeine.newBuilder().build();
    }

    public Restaurant getRestaurant(){
        return restaurantCache.getIfPresent(CACHE_KEY);
    }

    public void putRestaurant(Restaurant restaurant){
        restaurantCache.put(CACHE_KEY, restaurant);
    }
}
