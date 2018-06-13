package com.ifood.mqtt.ifoodmqttmanagement.infrastructure;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.util.Map;
import java.util.Properties;

@Configuration
public class Config {

    private static final String MQTT_CLIENT_SETTINGS_PREFIX = "mqttclient.properties";
    private static final String INTEGRATION_SETTINGS_PREFIX = "integration.properties";

    public Map<String, String> getMqttClientSettings() {
        return getSettings(MQTT_CLIENT_SETTINGS_PREFIX);
    }

    public Map<String, String> getIntegrationSettings() {
        return getSettings(INTEGRATION_SETTINGS_PREFIX);
    }

    private Properties loadIfoodProperties() {
        YamlPropertiesFactoryBean properties = new YamlPropertiesFactoryBean();
        properties.setResources(new ClassPathResource("application.yml"));
        return properties.getObject();
    }

    private Map<String, String> getSettings(String prefix) {
        Properties yaml = loadIfoodProperties();
        MapConfigurationPropertySource source = new MapConfigurationPropertySource(yaml);
        return new Binder(source).bind(prefix, Bindable.mapOf(String.class, String.class)).get();
    }
}
