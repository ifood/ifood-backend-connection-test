package com.ifood.ifoodclient.service.command;

import com.ifood.ifoodclient.error.ApiException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
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
    private Integer qos;
    private String broker;

    public void sendKeepAlive(String payload, String clientId){

        try {
            MqttClient mqttClient =
                    new MqttClient(broker, clientId, new MemoryPersistence());

            this.connectToBroker(mqttClient, this.getMqttConnectOptions());
            this.publishMessageToBroker(mqttClient, payload);
            this.disconnect(mqttClient);

        } catch (MqttException me) {
            log.error("[" + clientId + "]" + " Error sending client keepAlive message");
            throw ApiException.builder()
                    .code(ApiException.INTEGRATION_ERROR)
                    .cause(me.getCause())
                    .message(me.getMessage())
                    .build();
        }
    }

    private void connectToBroker(MqttClient mqttClient, MqttConnectOptions connOpts) throws MqttException {
        log.info("Connecting to MQTT broker: " + broker);
        mqttClient.connect(connOpts);
        log.info("MQTT broker connected!");
    }

    private MqttConnectOptions getMqttConnectOptions(){
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        return connOpts;
    }

    private void publishMessageToBroker(MqttClient mqttClient, String payload) throws MqttException {
        log.info("Publishing message to MQTT broker: [ " + payload + " ]");
        mqttClient.publish(topic, this.getMqttMessage(payload));
        log.info("Client Message published successfully.");
    }

    private MqttMessage getMqttMessage(String payload){
        MqttMessage message = new MqttMessage(payload.getBytes());
        message.setQos(qos);
        return message;
    }

    private void disconnect(MqttClient mqttClient) throws MqttException {
        mqttClient.disconnect();
        log.info("Client successfully disconnected from MQTT broker.");
    }
}
