package br.com.ifood.connection;

import static org.apache.ignite.cache.CacheAtomicityMode.ATOMIC;

import javax.cache.configuration.FactoryBuilder.SingletonFactory;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteCheckedException;
import org.apache.ignite.IgniteSpring;
import org.apache.ignite.configuration.CacheConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import br.com.ifood.connection.cache.policy.OnlineStatusExpirePolicy;
import br.com.ifood.connection.data.entity.StatusEntity;
import br.com.ifood.connection.data.repository.StatusRepository;
import br.com.ifood.connection.mqtt.message.converter.RestaurantMessageConverter;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@IntegrationComponentScan
@EnableSwagger2
public class ApplicationConfig {

    @Value("${app.cache.restaurants.status}")
    private String cacheRestaurantsStatus;

    @Value("${app.offline.threshold}")
    private int offlineThreshold;

    @Bean
    public RestaurantMessageConverter restaurantMessageConverter() {
        return new RestaurantMessageConverter();
    }

    @Bean
    public Ignite ignite(ApplicationContext context, StatusRepository statusRepository)
            throws IgniteCheckedException {

        Ignite ignite = IgniteSpring.start("ignite-config.xml", context);

        return ignite;
    }

    @Bean
    @Qualifier("${app.cache.restaurants.status}")
    public IgniteCache<String, StatusEntity> getOnlineStatusCache(Ignite ignite) {
        CacheConfiguration<String, StatusEntity> cfg = new CacheConfiguration<String, StatusEntity>(
                cacheRestaurantsStatus)
                        .setAtomicityMode(ATOMIC)
                        .setEagerTtl(true);

        return ignite.getOrCreateCache(cfg);
    }

    @Bean
    public SingletonFactory<OnlineStatusExpirePolicy> getOnlineStatusExpirePolicyFactory(
            OnlineStatusExpirePolicy onlineStatusExpirePolicy) {
        return new SingletonFactory<>(onlineStatusExpirePolicy);
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();
    }

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }
}
