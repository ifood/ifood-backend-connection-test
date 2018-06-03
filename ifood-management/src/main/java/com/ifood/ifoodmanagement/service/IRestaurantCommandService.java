package com.ifood.ifoodmanagement.service;

import com.ifood.ifoodmanagement.domain.Restaurant;

public interface IRestaurantCommandService {

    Restaurant create(Restaurant restaurant);

    Restaurant patch(Restaurant existingRestaurant, Restaurant newRestaurant);
}
