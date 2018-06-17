package com.ifood.ifoodclient.service.command.ifood;

import com.ifood.ifoodclient.domain.Restaurant;

public interface IRestaurantCommandService {

    Restaurant patch(Restaurant existingRestaurant, Restaurant newRestaurant);
}
