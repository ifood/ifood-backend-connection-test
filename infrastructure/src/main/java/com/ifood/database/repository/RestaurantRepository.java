package com.ifood.database.repository;

import com.ifood.database.entity.RestaurantEntity;

public interface RestaurantRepository {

    RestaurantEntity findRestaurant(String restaurantCode);

    boolean notExists(String restaurantCode);

}
