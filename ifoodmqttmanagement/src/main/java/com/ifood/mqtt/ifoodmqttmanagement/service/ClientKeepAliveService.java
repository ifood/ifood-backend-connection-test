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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientKeepAliveService {

    @Value("${ifood.integration.ifoodmanagement.apiUrl}")
    private String hostApiUrl;

    public void createClientKeepAliveLog(String clientId, String isAvailable){

        ClientKeepAliveHttpClient keepAliveHttpClient = Feign.builder()
                .client(new OkHttpClient())
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .logger(new Slf4jLogger(ClientKeepAliveHttpClient.class))
                .logLevel(Logger.Level.FULL)
                .target(ClientKeepAliveHttpClient.class, hostApiUrl);

        ClientKeepAliveLog requestBody = ClientKeepAliveLog.builder()
                .restaurantCode(clientId)
                .available(isAvailable)
                .build();

        keepAliveHttpClient.create(requestBody);
    }
}
