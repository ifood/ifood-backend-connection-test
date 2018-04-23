package com.ifood.mqtt;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;

@Configuration
class MqttConfiguration {

    @Autowired
    private MqttProperties mqttProperties;

    private static final MessageChannel mqttInputChannel = new DirectChannel();

    @Bean
    public MessageProducer inbound() {
        MqttPahoMessageDrivenChannelAdapter adapter =  new MqttPahoMessageDrivenChannelAdapter(
                mqttProperties.getMqttHealthEndpoint(),
                MqttClient.generateClientId(), //
                MqttTopics.RESTAURANT_HEALTH_SIGNAL_TOPIC);
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);
        adapter.setOutputChannel(mqttInputChannel);
        return adapter;
    }
}
