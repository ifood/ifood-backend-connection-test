package com.ifood.mqtt.ifoodmqttmanagement.service;

import com.ifood.mqtt.ifoodmqttmanagement.domain.Restaurant;
import com.ifood.mqtt.ifoodmqttmanagement.infrastructure.Config;
import com.ifood.mqtt.ifoodmqttmanagement.restinterfaces.RestaurantHttpClient;
import feign.Feign;
import feign.Logger;
import feign.httpclient.ApacheHttpClient;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.slf4j.Slf4jLogger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final Config configuration;

    private Map<String, String> properties;

    @PostConstruct
    protected void setup(){
        properties = configuration.getMqttClientSettings();
    }

    public void patchRestaurantAvailability(String isAvailable){

        RestaurantHttpClient httpClient = Feign.builder()
                .client(new ApacheHttpClient())
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .logger(new Slf4jLogger(RestaurantHttpClient.class))
                .logLevel(Logger.Level.FULL)
                .target(RestaurantHttpClient.class,
                        configuration.getIntegrationSettings().get("managementApiUrl"));

        Restaurant restaurant = Restaurant.builder()
                .loggedIn(null)         // do not update on manager side
                .sendKeepAlive(null)    // also.
                .available(isAvailable)
                .build();

        httpClient.patch(restaurant);
    }
}
