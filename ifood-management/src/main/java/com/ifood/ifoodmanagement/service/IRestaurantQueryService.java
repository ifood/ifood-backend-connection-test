package com.ifood.ifoodmanagement.service;

import com.ifood.ifoodmanagement.domain.Restaurant;
import com.ifood.ifoodmanagement.domain.RestaurantStatusLog;
import org.joda.time.Interval;

import java.util.List;
import java.util.Optional;

public interface IRestaurantQueryService {

    Optional<Restaurant> findByCode(String code);

    List<Restaurant> findAll();

    List<Restaurant> fetchRestaurantsOnlineStatus(List<String> restaurantCodes);

    List<RestaurantStatusLog> fetchRestaurantAvailabilityHistory(String code, String status, Interval dateInterval);
}
