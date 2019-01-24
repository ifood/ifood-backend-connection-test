package br.com.ifood.ifoodbackendconnection.health;

import br.com.ifood.ifoodbackendconnection.configuration.HealthCheckConfiguration;
import br.com.ifood.ifoodbackendconnection.utilities.Tuple2;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.springframework.boot.actuate.health.Health.unknown;

@Component
@Slf4j
public class ServiceDependencyHealthIndicator implements HealthIndicator {

    private final Executor healthIndicatorExecutor;
    private final HealthCheckConfiguration configuration;

    private AtomicReference<Optional<Tuple2<DateTime, Health>>> lastHealth = new AtomicReference<>(Optional.empty());
    private AtomicBoolean running = new AtomicBoolean(true);

    @Autowired
    public ServiceDependencyHealthIndicator(final Executor healthIndicatorExecutor, HealthCheckConfiguration configuration) {
        this.healthIndicatorExecutor = healthIndicatorExecutor;
        this.configuration = configuration;
    }

    @PostConstruct
    private void start() {
        this.healthIndicatorExecutor.execute(() -> {
            while (running.get()) {
                checkHealth();
                try {
                    Thread.sleep(this.configuration.getFrequency() * 1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
    }

    @Override
    public Health health() {
        return lastHealth.get().orElse(new Tuple2<>(DateTime.now(), unknown().build())).get_2();
    }

    void checkHealth() {
        try {
            lastHealth.set(Optional.of(new Tuple2<>(DateTime.now(), Health.up().build())));
        } catch (Exception e) {
            log.warn("Health check failed", e);
            lastHealth.set(Optional.of(new Tuple2<>(DateTime.now(), Health.down().withException(e).build())));
        }
    }
}