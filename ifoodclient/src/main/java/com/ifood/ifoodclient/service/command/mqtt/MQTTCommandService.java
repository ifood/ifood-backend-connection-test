package com.ifood.ifoodclient.service.command.mqtt;

import com.ifood.ifoodclient.domain.QoSEnum;
import com.ifood.ifoodclient.error.ApiException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
@Data
@Slf4j
@RequiredArgsConstructor
public class MQTTCommandService {

    @Value("${mqttclient.properties.topic}")
    private String topic;

    @Value("${mqttclient.properties.broker}")
    private String broker;

    @Value("${mqttclient.properties.clientId}")
    private String clientId;

    private MqttAsyncClient mqttAsyncClient;
    private final MQTTCallbackListener callbackListener;

    @PostConstruct
    private void setupAsyncClient(){
        try {
            mqttAsyncClient = new MqttAsyncClient
                    (broker, clientId, new MemoryPersistence());
            mqttAsyncClient.setCallback(callbackListener);
        } catch (MqttException me) {
            log.error("[C] Error creating MqttAsyncClient instance...");
            throw ApiException.builder()
                    .code(ApiException.INTERNAL_ERROR)
                    .cause(me.getCause())
                    .message(me.getMessage())
                    .reason("Error creating MqttAsyncClient instance...")
                    .build();
        }
    }

    public void sendKeepAlive(String payload, String clientId){

        try {
            this.connectToBroker(mqttAsyncClient, this.getMqttConnectOptions());
            this.publishMessage(mqttAsyncClient, payload);
            this.disconnect(mqttAsyncClient);
        } catch (MqttException me) {
            log.error("[" + clientId + "]" + " Error sending client KeepAlive message");
            throw ApiException.builder()
                    .code(ApiException.INTEGRATION_ERROR)
                    .cause(me.getCause())
                    .message(me.getMessage())
                    .build();
        }
    }

    private void connectToBroker(MqttAsyncClient mqttAsyncClient, MqttConnectOptions connOpts) throws MqttException {
        log.info("[C] Connecting to broker: " + broker);
        mqttAsyncClient.connect(connOpts);
        log.info("[C] Broker connected!");
    }

    private MqttConnectOptions getMqttConnectOptions(){
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        return connOpts;
    }

    private void publishMessage(MqttAsyncClient mqttAsyncClient, String payload) throws MqttException {
        log.info("[C] Publishing message to broker: [" + payload + "]");
        mqttAsyncClient.publish(topic, this.getMqttMessage(payload));
        log.info("[C] Client message published successfully.");
    }

    private MqttMessage getMqttMessage(String payload){
        MqttMessage message = new MqttMessage(payload.getBytes());
        message.setQos(QoSEnum.EXACTLY_ONCE.getLevel());
        return message;
    }

    private void disconnect(MqttAsyncClient mqttAsyncClient) throws MqttException {
        mqttAsyncClient.disconnect();
        log.info("[C] MqttAsyncClient successfully disconnected from broker.");
    }
}
