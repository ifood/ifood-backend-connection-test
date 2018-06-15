package com.ifood.ifoodclient.infrastructure;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.ifood.ifoodclient.domain.Restaurant;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@Component
@NoArgsConstructor
public class CacheBean {

    @Value("${cachesettings.defaultKey}")
    private String CACHE_KEY;

    private Cache<String, Restaurant> restaurantCache;

    @PostConstruct
    private void initializeCache(){
        restaurantCache = Caffeine.newBuilder().build();
    }

    public Restaurant getLoggedRestaurant(){
        return restaurantCache.getIfPresent(CACHE_KEY);
    }

    public void updateLoggedRestaurant(Restaurant restaurant){
        restaurantCache.put(CACHE_KEY, restaurant);
    }
}
