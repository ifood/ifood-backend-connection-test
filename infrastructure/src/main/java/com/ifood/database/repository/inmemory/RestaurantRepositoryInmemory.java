package com.ifood.database.repository.inmemory;

import com.ifood.database.entity.RestaurantEntity;
import com.ifood.database.repository.RestaurantRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
class RestaurantRepositoryInmemory implements RestaurantRepository {

    private final List<RestaurantEntity> list = List.of( //
            new RestaurantEntity("restaurant1"),
            new RestaurantEntity("restaurant2"),
            new RestaurantEntity("restaurant3"),
            new RestaurantEntity("restaurant4")
    );

    @Override
    public RestaurantEntity findRestaurant(String restaurantCode) {
        return list.stream()//
                .filter(restaurantEntity -> //
                        restaurantEntity.getCode().equals(restaurantCode) //
                ) //
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Restaurant not found."));
    }

    @Override
    public boolean notExists(String restaurantCode) {
        return findRestaurant(restaurantCode) == null;
    }

}
