package com.ifood.ifoodmanagement.service.query;

import com.ifood.ifoodmanagement.domain.ClientKeepAliveLog;
import com.ifood.ifoodmanagement.domain.Restaurant;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Optional;

public interface IRestaurantQueryService {

    Optional<Restaurant> findByCode(String code);

    List<Restaurant> findAll();

    List<Restaurant> fetchRestaurantsOnlineStatus(List<String> restaurantCodes);

    List<ClientKeepAliveLog> fetchRestaurantAvailabilityHistory(String code, boolean available, DateTime from, DateTime to);
}
