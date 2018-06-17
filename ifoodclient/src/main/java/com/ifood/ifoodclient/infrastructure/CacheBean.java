package com.ifood.ifoodclient.infrastructure;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.ifood.ifoodclient.domain.Restaurant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;

@Slf4j
@Component
@Getter
@Setter
@Scope(value = "singleton", proxyMode = ScopedProxyMode.TARGET_CLASS)
@NoArgsConstructor
public class CacheBean {

    private String cacheKey;

    private Cache<String, Restaurant> restaurantCache;

    @PostConstruct
    private void initializeCache(){
        restaurantCache = Caffeine.newBuilder().build();
    }

    public Restaurant getLoggedRestaurant(){
        return restaurantCache.getIfPresent(cacheKey);
    }

    public void updateLoggedRestaurant(Restaurant restaurant){
        restaurantCache.put(restaurant.getCode(), restaurant);
    }
}
