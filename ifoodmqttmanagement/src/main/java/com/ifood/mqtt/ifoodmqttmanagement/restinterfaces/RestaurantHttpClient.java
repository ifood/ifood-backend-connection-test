package com.ifood.mqtt.ifoodmqttmanagement.restinterfaces;

import com.ifood.mqtt.ifoodmqttmanagement.domain.Restaurant;
import feign.Headers;
import feign.RequestLine;

public interface RestaurantHttpClient {

    @RequestLine("PATCH /restaurant/{code}")
    @Headers("Content-Type: application/json")
    void patch(Restaurant restaurant);
}
