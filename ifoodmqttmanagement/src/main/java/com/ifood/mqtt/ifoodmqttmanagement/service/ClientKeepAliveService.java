package com.ifood.mqtt.ifoodmqttmanagement.service;

import com.ifood.mqtt.ifoodmqttmanagement.domain.ClientKeepAliveLog;
import com.ifood.mqtt.ifoodmqttmanagement.infrastructure.Config;
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

import javax.annotation.PostConstruct;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientKeepAliveService {

    private final Config configuration;
    private Map<String, String> properties;

    @PostConstruct
    protected void setup(){
        properties = configuration.getMqttClientSettings();
    }

    public void createClientKeepAliveLog(String clientId, String isAvailable){

        ClientKeepAliveHttpClient httpClient = Feign.builder()
                .client(new OkHttpClient())
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .logger(new Slf4jLogger(ClientKeepAliveHttpClient.class))
                .logLevel(Logger.Level.FULL)
                .target(ClientKeepAliveHttpClient.class,
                        configuration.getIntegrationSettings().get("managementApiUrl"));

        ClientKeepAliveLog requestBody = ClientKeepAliveLog.builder()
                .restaurantCode(clientId)
                .available(isAvailable)
                .build();

        httpClient.create(requestBody);
    }
}
