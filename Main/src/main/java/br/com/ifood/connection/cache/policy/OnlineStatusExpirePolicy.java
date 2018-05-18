package br.com.ifood.connection.cache.policy;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.Serializable;
import javax.cache.configuration.Factory;
import javax.cache.configuration.FactoryBuilder;
import javax.cache.expiry.Duration;
import javax.cache.expiry.ExpiryPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Expire policy for cache online status.
 */
@Component
public class OnlineStatusExpirePolicy implements ExpiryPolicy, Serializable {

    @Value("${app.offline.threshold}")
    private int offlineThreshold;

    public static Factory<OnlineStatusExpirePolicy> factoryOf() {
        return new FactoryBuilder.SingletonFactory<>(new OnlineStatusExpirePolicy());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Duration getExpiryForCreation() {
        return new Duration(SECONDS, offlineThreshold);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Duration getExpiryForAccess() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Duration getExpiryForUpdate() {
        return new Duration(SECONDS, offlineThreshold);
    }
}
