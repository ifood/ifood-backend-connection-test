package com.ifood.ifoodclient.service.command.ifood;

import com.ifood.mqtt.ifoodmqttmanagement.client.MQTTClientManager;
import com.ifood.mqtt.ifoodmqttmanagement.error.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class IfoodMqttService {

    private final MQTTClientManager mqttClientManager;

    public void sendKeepAlive(String message){

        try {
            mqttClientManager.sendKeepAlive(message);
        } catch (MqttException e) {
            log.error("Internal error in sendKeepAlive process...");
            throw ApiException.builder()
                    .code(ApiException.INTERNAL_ERROR)
                    .message("Internal error in sendKeepAlive process...")
                    .build();
        }
    }
}
