package com.ifood.ifoodclient.service.command;

import com.ifood.ifoodclient.domain.Restaurant;

public interface IRestaurantCommandService {

    Restaurant create(Restaurant restaurant);

    Restaurant patch(Restaurant existingRestaurant, Restaurant newRestaurant);

    void insertRestaurantStatusLog(Restaurant restaurant);
}
