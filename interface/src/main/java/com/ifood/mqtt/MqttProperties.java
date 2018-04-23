package com.ifood.mqtt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Reader of the externalized of the configurations
 *
 * Following the 12-factor practice
 */
@Component
class MqttProperties {

    @Value("${ifood.mqtt.health.host}")
    private String mqttHealthHost;

    @Value("${ifood.mqtt.health.port}")
    private String mqttHealthPort;

    public String getMqttHealthEndpoint() {
        return mqttHealthHost + ":" + mqttHealthPort;
    }
}
