package com.ifood.ifoodclient.service.command.mqtt;

import com.ifood.ifoodclient.error.ApiException;
import com.ifood.ifoodclient.infrastructure.CacheBean;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

@Service
@Data
@Slf4j
@ConfigurationProperties(prefix = "mqttclient.properties")
public class MQTTCommandService {

    private String topic;
    private Integer qos;  // "exactly-once"(2) - most realiable, but a little slower
    private String broker;

    private MqttAsyncClient mqttAsyncClient;
    private MQTTCallbackListener callbackListener;
    private CacheBean cacheBean;

    public MQTTCommandService(MQTTCallbackListener callbackListener, CacheBean cacheBean){

        this.callbackListener = callbackListener;

        try {
            mqttAsyncClient = new MqttAsyncClient
                    (broker, cacheBean.getRestaurant().getCode(), new MemoryPersistence());
            mqttAsyncClient.setCallback(getCallbackListener());
        } catch (MqttException me) {
            log.error("Error creating MqttAsyncClient instance...");
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
        log.info("Connecting to MQTT broker: " + broker);
        mqttAsyncClient.connect(connOpts);
        log.info("MQTT broker connected!");
    }

    private MqttConnectOptions getMqttConnectOptions(){
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        return connOpts;
    }

    private void publishMessage(MqttAsyncClient mqttAsyncClient, String payload) throws MqttException {
        log.info("Publishing message to MQTT broker: [ " + payload + " ]");
        mqttAsyncClient.publish(topic, this.getMqttMessage(payload));
        log.info("Client Message published successfully.");
    }

    private MqttMessage getMqttMessage(String payload){
        MqttMessage message = new MqttMessage(payload.getBytes());
        message.setQos(qos);
        return message;
    }

    private void disconnect(MqttAsyncClient mqttAsyncClient) throws MqttException {
        mqttAsyncClient.disconnect();
        log.info("Client successfully disconnected from MQTT broker.");
    }
}
