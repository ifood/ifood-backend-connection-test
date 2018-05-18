package br.com.ifood.connection.mqtt.message;

/**
 * The keepalive message.<br/>
 * Sent when the client connects to the MQTT broker.
 */
public class KeepaliveMessage extends AbstractRestaurantMessage {

    public KeepaliveMessage(Long restaurantId) {
        super(restaurantId);
    }
}
