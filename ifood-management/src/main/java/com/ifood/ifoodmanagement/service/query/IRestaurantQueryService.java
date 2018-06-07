package com.ifood.ifoodmanagement.service.query;

import com.ifood.ifoodmanagement.domain.Restaurant;
import com.ifood.ifoodmanagement.domain.ClientKeepAliveLog;
import org.joda.time.Interval;

import java.util.List;
import java.util.Optional;

public interface IRestaurantQueryService {

    Optional<Restaurant> findByCode(String code);

    List<Restaurant> findAll();

    List<Restaurant> fetchRestaurantsOnlineStatus(List<String> restaurantCodes);

    List<ClientKeepAliveLog> fetchRestaurantAvailabilityHistory(String code, String status, Interval dateInterval);
}
