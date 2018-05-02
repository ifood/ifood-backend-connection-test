package br.com.ifood.connection.mqtt.message;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public abstract class AbstractRestaurantMessage implements MqttRestaurantMessage {

    private Long restaurantId;
}
