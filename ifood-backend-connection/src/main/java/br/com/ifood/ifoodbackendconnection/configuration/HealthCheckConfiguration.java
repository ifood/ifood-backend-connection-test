package br.com.ifood.ifoodbackendconnection.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "healthcheck")
public class HealthCheckConfiguration {
    private int frequency;

    /**
     * Frequency to run the health-check in seconds
     * @return
     */
    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(final int frequency) {
        this.frequency = frequency;
    }


}
