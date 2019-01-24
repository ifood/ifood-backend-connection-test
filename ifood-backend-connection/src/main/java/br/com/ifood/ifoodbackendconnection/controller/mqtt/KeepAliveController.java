package br.com.ifood.ifoodbackendconnection.controller.mqtt;

import br.com.ifood.ifoodbackendconnection.service.HealthCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.MessageHandler;
import org.springframework.stereotype.Controller;

@Controller
public class KeepAliveController {

    private HealthCheckService healthCheckService;

    @Autowired
    public KeepAliveController(HealthCheckService healthCheckService) {
        this.healthCheckService = healthCheckService;
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler handler() {
        return message -> healthCheckService.saveSignalForRestaurant(message.getPayload().toString());
    }
}