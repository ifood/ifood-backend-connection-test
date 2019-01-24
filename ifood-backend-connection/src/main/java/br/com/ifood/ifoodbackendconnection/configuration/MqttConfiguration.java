package br.com.ifood.ifoodbackendconnection.configuration;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;

@Configuration
@EnableIntegration
public class MqttConfiguration {

    @Value("${mqtt.host}")
    private String mqttHost;

    @Value("${mqtt.port}")
    private String mqttPort;

    @Value("${mqtt.topic}")
    private String mqttTopic;

    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageProducer inbound() {
        MqttPahoMessageDrivenChannelAdapter adapter =  new MqttPahoMessageDrivenChannelAdapter(
                getEndpoint(),
                MqttClient.generateClientId(),
                mqttTopic);
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);
        adapter.setOutputChannel(mqttInputChannel());
        return adapter;
    }

    public String getEndpoint() {
        return String.format("%s:%s", mqttHost, mqttPort);
    }
}