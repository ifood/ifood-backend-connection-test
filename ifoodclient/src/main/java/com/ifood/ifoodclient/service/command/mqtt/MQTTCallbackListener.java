package com.ifood.ifoodclient.service.command.mqtt;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MQTTCallbackListener implements MqttCallback {

    @Override
    public void connectionLost(Throwable throwable) {
        log.error("Client lost connection to MQTT broker. KeepAlive not sent...");
    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        log.info("Client KeepAlive message arrived in MQTT broker.");
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        log.info("Client KeepAlive message successfully delivered!");
    }
}
