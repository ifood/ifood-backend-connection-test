package br.com.ifood.connection.mqtt.message.converter;

import br.com.ifood.connection.mqtt.message.MqttRestaurantMessage;

@FunctionalInterface
/**
 * Converts a MQTT byte[] payload to a MqttRestaurantMessage
 */
public interface  PayloadConverter {

    MqttRestaurantMessage convert(byte[] payload);
}
