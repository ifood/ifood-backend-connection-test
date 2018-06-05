package com.ifood.ifoodclient.infrastructure;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.ifood.ifoodclient.domain.Restaurant;
import com.ifood.ifoodclient.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CommandLineAppStartupRunner implements CommandLineRunner {

    private final RestaurantRepository restaurantRepository;

    @Value("${cachesettings.defaultKey}")
    private String CACHE_KEY;

    @Override
    public void run(String... args) throws Exception {

        final Optional<Restaurant> firstByLoggedIn = restaurantRepository.findDistinctFirstByLoggedInFalse();

        if (firstByLoggedIn.isPresent()){
            Restaurant firstNotLoggedIn = firstByLoggedIn.get();
            firstNotLoggedIn.setLoggedIn(true);
            restaurantRepository.save(firstNotLoggedIn);

            // Put it on cache, so we can retrieve it later.
            Cache<String, Restaurant> cache = Caffeine.newBuilder().maximumSize(2).build();
            cache.put(CACHE_KEY, firstNotLoggedIn);
        }
    }
}
