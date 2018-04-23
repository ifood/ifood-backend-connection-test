package com.ifood.mqtt.health;

import com.ifood.service.health.HealthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.MessageHandler;
import org.springframework.stereotype.Controller;

@Controller
class HealthControllerMqtt {

    @Autowired
    private HealthService healthService;

    /**
     * Receive the signal sent by the restaurant.
     * @return the message handler.
     */
    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler handler() {
        return message -> healthService.receiveHealthSignal(message.getPayload().toString());
    }

}
