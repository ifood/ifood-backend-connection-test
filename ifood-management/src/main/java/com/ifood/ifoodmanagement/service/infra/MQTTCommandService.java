package com.ifood.ifoodmanagement.service.infra;

import com.ifood.ifoodmanagement.domain.QoSEnum;
import com.ifood.ifoodmanagement.error.ApiException;
import com.ifood.ifoodmanagement.service.command.KeepAliveCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Slf4j
@Service
@RequiredArgsConstructor
public class MQTTCommandService implements MqttCallback, IMqttActionListener {

    @Value("${mqttclient.properties.topic}")
    private String topic;

    @Value("${mqttclient.properties.broker}")
    private String broker;

    @Value("${mqttclient.properties.clientId}")
    private String clientId;

    private final KeepAliveCommandService keepAliveService;

    @PostConstruct
    private void setupAsyncClient(){
        try {
            final MqttAsyncClient mqttAsyncClient = new MqttAsyncClient
                    (broker, clientId, new MemoryPersistence());

            mqttAsyncClient.setCallback(this);
            this.connectToBroker(mqttAsyncClient);

            mqttAsyncClient.subscribe
                    (topic, QoSEnum.EXACTLY_ONCE.getLevel(), "[M] MQTT topic subscription", this);
        } catch (MqttException me) {
            log.error("[M] Error creating MqttAsyncClient subscription instance...");
            throw ApiException.builder()
                    .code(ApiException.INTERNAL_ERROR)
                    .cause(me.getCause())
                    .message(me.getMessage())
                    .reason("Error creating MqttAsyncClient subscription instance...")
                    .build();
        }
    }

    private void connectToBroker(MqttAsyncClient mqttAsyncClient) throws MqttException {
        log.info("[M] Connecting to broker: " + broker);
        mqttAsyncClient.connect(getMqttConnectOptions());
        log.info("[M] Broker connected!");
    }

    private MqttConnectOptions getMqttConnectOptions(){
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        return connOpts;
    }

    @Override
    public void connectionLost(Throwable throwable) {
        log.error("[M] Lost connection to broker...");
    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        String clientCode = getClientCodeFromMqttMessage(mqttMessage);
        log.info("[M] Incoming KeepAlive message from client [%s]", clientCode);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        try {
            keepAliveService.insertClientKeepAliveLog
                    (getClientCodeFromMqttMessage(iMqttDeliveryToken.getMessage()));
        } catch (MqttException e) {
            log.error("[M] Could not extract client information from incoming payload...");
        }
    }

    @Override
    public void onSuccess(IMqttToken iMqttToken) {
    }

    @Override
    public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
    }

    private String getClientCodeFromMqttMessage(MqttMessage mqttMessage){
        return StringUtils.substringBetween(new String(mqttMessage.getPayload()),"[", "]");
    }
}
