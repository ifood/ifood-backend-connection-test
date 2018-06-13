package com.ifood.mqtt.ifoodmqttmanagement.service;

import com.ifood.mqtt.ifoodmqttmanagement.domain.ClientKeepAliveLog;
import com.ifood.mqtt.ifoodmqttmanagement.restinterfaces.ClientKeepAliveHttpClient;
import feign.Feign;
import feign.Logger;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientKeepAliveService {

    // For some reason, Spring 2.0 cannot bind this property in runtime... =*(
    private static final String INTEGRATION_API_URL = "http://localhost:8097/ifoodmanagement";

    public void createClientKeepAliveLog(String clientId, String isAvailable){

        ClientKeepAliveHttpClient keepAliveHttpClient = Feign.builder()
                .client(new OkHttpClient())
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .logger(new Slf4jLogger(ClientKeepAliveHttpClient.class))
                .logLevel(Logger.Level.FULL)
                .target(ClientKeepAliveHttpClient.class, INTEGRATION_API_URL);

        ClientKeepAliveLog requestBody = ClientKeepAliveLog.builder()
                .restaurantCode(clientId)
                .available(isAvailable)
                .build();

        keepAliveHttpClient.create(requestBody);
    }
}
