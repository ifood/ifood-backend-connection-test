package com.ifood.ifoodclient.service.command;

import com.ifood.ifoodclient.domain.Restaurant;

public interface IRestaurantCommandService {

    Restaurant patch(Restaurant existingRestaurant, Restaurant newRestaurant);

    void insertRestaurantStatusLog(Restaurant restaurant);
}
