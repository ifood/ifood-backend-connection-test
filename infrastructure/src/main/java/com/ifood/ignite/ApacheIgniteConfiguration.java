package com.ifood.ignite;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteException;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.configuration.*;
import org.apache.ignite.springdata.repository.config.EnableIgniteRepositories;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableIgniteRepositories
class ApacheIgniteConfiguration {

    private static final String CONNECTION_GRID = "restaurantConnectionGrid";
    private static final int PORT = 47100;

    @Bean
    public IgniteConfiguration igniteCfg() {
        IgniteConfiguration igniteConfiguration = new IgniteConfiguration();
        igniteConfiguration.setClientMode(false);
        igniteConfiguration.setMetricsLogFrequency(0);
        igniteConfiguration.setQueryThreadPoolSize(2);
        igniteConfiguration.setDataStreamerThreadPoolSize(1);
        igniteConfiguration.setManagementThreadPoolSize(2);
        igniteConfiguration.setPublicThreadPoolSize(2);
        igniteConfiguration.setSystemThreadPoolSize(2);
        igniteConfiguration.setRebalanceThreadPoolSize(1);
        igniteConfiguration.setAsyncCallbackPoolSize(2);
        igniteConfiguration.setPeerClassLoadingEnabled(false);
        igniteConfiguration.setIgniteInstanceName(CONNECTION_GRID);
        BinaryConfiguration binaryConfiguration = new BinaryConfiguration();
        binaryConfiguration.setCompactFooter(false);
        igniteConfiguration.setBinaryConfiguration(binaryConfiguration);

        configurePersistence(igniteConfiguration);

        // connector configuration
        ConnectorConfiguration connectorConfiguration = new ConnectorConfiguration();
        connectorConfiguration.setPort(PORT);


        CacheConfiguration restaurantConnectionHealthConfig = new CacheConfiguration();
        restaurantConnectionHealthConfig.setCopyOnRead(false);
        restaurantConnectionHealthConfig.setBackups(0);
        restaurantConnectionHealthConfig.setAtomicityMode(CacheAtomicityMode.ATOMIC);
        restaurantConnectionHealthConfig.setName("RestaurantConnectionHealth");
        restaurantConnectionHealthConfig.setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(new Duration(TimeUnit.MINUTES, 2)));

        igniteConfiguration.setCacheConfiguration(restaurantConnectionHealthConfig);
        return igniteConfiguration;
    }

    private void configurePersistence(IgniteConfiguration igniteConfiguration) {
        DataStorageConfiguration dataStorageConfiguration = new DataStorageConfiguration();
        dataStorageConfiguration.setWalArchivePath("./data/walArchive");
        igniteConfiguration.setDataStorageConfiguration(dataStorageConfiguration);
    }

    @Bean(destroyMethod = "close")
    public Ignite igniteInstance(IgniteConfiguration igniteConfiguration) throws IgniteException {
        IgniteConfiguration cfg = new IgniteConfiguration();
        return Ignition.start(igniteConfiguration);
    }


}
