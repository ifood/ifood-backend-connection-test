package com.ifood.ifoodclient.service.query;

import com.ifood.ifoodclient.domain.Restaurant;

import java.util.Optional;

public interface IRestaurantQueryService {

    Optional<Restaurant> findByCode(String code);
}
