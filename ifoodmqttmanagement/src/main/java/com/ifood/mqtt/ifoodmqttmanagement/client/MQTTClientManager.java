package com.ifood.mqtt.ifoodmqttmanagement.client;

import com.ifood.mqtt.ifoodmqttmanagement.domain.QoSLevel;
import com.ifood.mqtt.ifoodmqttmanagement.error.ApiException;
import com.ifood.mqtt.ifoodmqttmanagement.infrastructure.Config;
import com.ifood.mqtt.ifoodmqttmanagement.service.ClientKeepAliveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
@Qualifier("mqttClientManager")
public class MQTTClientManager implements MqttCallback, IMqttActionListener {

    private final Config configuration;
    private final ClientKeepAliveService clientKeepAliveService;

    private MqttAsyncClient publisherAsyncClient;
    private IMqttToken connectToken;
    private Map<String, String> properties;

    @PostConstruct
    protected void setupAsyncClient(){
        properties = configuration.getMqttClientSettings();
        this.createAsyncConnection();
    }

    // --------------------- EXPOSED METHODS ----------------------

    public void sendKeepAlive(String payload) throws MqttException {
        this.publishMqttMessage(payload);
    }

    // --------------------- INTERNAL METHODS ---------------------

    private void createAsyncConnection() {

        try {
            publisherAsyncClient = new MqttAsyncClient(
                    properties.get("broker"),
                    properties.get("clientId"),
                    new MemoryPersistence());

            publisherAsyncClient.setCallback(this);

            connectToken = publisherAsyncClient.connect(getMqttConnectOptions());
            connectToken.waitForCompletion();

            publisherAsyncClient.subscribe(
                    properties.get("topic"),
                    QoSLevel.EXACTLY_ONCE.getLevel());
        } catch (MqttException me) {
            log.error("Error creating MqttAsyncClient...");
            throw ApiException.builder()
                    .code(ApiException.INTERNAL_ERROR)
                    .cause(me.getCause())
                    .message(me.getMessage())
                    .reason("Error creating MqttAsyncClient...")
                    .build();
        }
    }

    private MqttConnectOptions getMqttConnectOptions(){
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        return connOpts;
    }

    private MqttMessage createMqttMessage(String payload){
        MqttMessage message = new MqttMessage(payload.getBytes());
        message.setQos(QoSLevel.EXACTLY_ONCE.getLevel());
        return message;
    }

    private void publishMqttMessage(String payload) throws MqttException {
        log.info("Publishing message to broker.");
        publisherAsyncClient.publish(
                properties.get("topic"),
                this.createMqttMessage(payload));
        log.info("Client message published successfully.");
    }

    private boolean isConnected(MqttAsyncClient asyncClient) {
        return (!Objects.isNull(asyncClient)) && (asyncClient.isConnected());
    }

    private String getPayloadFromMqttMessage(MqttMessage mqttMessage){
        return new String(mqttMessage.getPayload());
    }

    private String getClientCodeFromMqttMessage(String payload){
        return StringUtils.substringBetween(payload,"[", "]");
    }

    private String getClientAvailabilityFromMqttMessage(String payload){
        return StringUtils.substringBetween(payload,"(", ")");
    }

    // --------------------- CALLBACK METHODS ---------------------

    @Override
    public void onSuccess(IMqttToken iMqttToken) {
        log.info("Success retrieving connection token from MqttAsyncClient.");
    }

    /**
     * Fallback method for failed delivery attempt
     *
     * @param iMqttToken
     * @param throwable
     */
    @Override
    public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
        this.createAsyncConnection();
    }

    @Override
    public void connectionLost(Throwable throwable) {
        log.warn("Lost connection to broker. Reconnect attempt in progress...");
        this.onFailure(connectToken, throwable);
    }

    /**
     * Invoked when a message has arrived from the MQTT broker.
     * It doesn't send back an ACK to the server until this method returns cleanly!
     *
     * @param topic
     * @param mqttMessage
     * @throws Exception
     */
    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) {
        final String message = getPayloadFromMqttMessage(mqttMessage);
        String clientId = getClientCodeFromMqttMessage(message);
        String clientAvailable = getClientAvailabilityFromMqttMessage(message);
        log.info(String.format("Incoming KeepAlive message from client [%s]", clientId));
        log.info(String.format("Message received is: %s", message));
        clientKeepAliveService.createClientKeepAliveLog(clientId, clientAvailable);
    }

    /**
     * Delivery for a message has been completed and all ACKs have been received.
     * @param iMqttDeliveryToken
     */
    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        log.info("Message successfully delivered.");
    }
}