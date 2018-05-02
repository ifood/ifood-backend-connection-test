package br.com.ifood.connection.mqtt.consumer;

import static br.com.ifood.connection.data.entity.status.StatusType.ONLINE;
import static br.com.ifood.connection.data.entity.status.StatusType.UNAVAILABLE;
import static java.time.temporal.ChronoUnit.SECONDS;

import br.com.ifood.connection.cache.policy.OnlineStatusExpirePolicy;
import br.com.ifood.connection.cache.util.CacheUtil;
import br.com.ifood.connection.data.entity.StatusEntity;
import br.com.ifood.connection.data.repository.StatusRepository;
import br.com.ifood.connection.mqtt.message.AbstractRestaurantMessage;
import br.com.ifood.connection.mqtt.message.KeepaliveMessage;
import br.com.ifood.connection.mqtt.message.ScheduleMessage;
import br.com.ifood.connection.mqtt.message.converter.RestaurantMessageConverter;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import javax.cache.configuration.FactoryBuilder.SingletonFactory;
import lombok.RequiredArgsConstructor;
import org.apache.ignite.IgniteCache;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

@Configuration
@RequiredArgsConstructor
/**
 * Consumer for the keepalives that restaurants send.<br/>
 * The keepalives are consumed from a MQTT broker.
 */
public class MqttConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(MqttClient.class);

    @Value("${mqtt.topic.connection}")
    private String topicConnection;

    @Value("${mqtt.topic.schedule}")
    private String topicSchedule;

    @Value("${mqtt.topic.qos}")
    private Integer topicQos;

    @Value("${mqtt.hostname}")
    private String brokerHostname;

    @Value("${mqtt.port}")
    private String brokerPort;

    @Value("${app.offline.threshold}")
    private int offlineThreshold;

    private final StatusRepository statusRepository;

    @Qualifier(value = "restaurants-status")
    private final IgniteCache<String, StatusEntity> onlineStatusCache;

    private final SingletonFactory<OnlineStatusExpirePolicy> factoryOnlineStatusExpirePolicy;

    @Bean
    public MessageProducer createMqttAdapter(RestaurantMessageConverter converter) {
        DefaultMqttPahoClientFactory clientFactory = new DefaultMqttPahoClientFactory();
        clientFactory.setCleanSession(false);

        MqttPahoMessageDrivenChannelAdapter adapter =
            new MqttPahoMessageDrivenChannelAdapter(getBrokerUrl(),
                MqttClient.generateClientId(),
                clientFactory,
                topicConnection, topicSchedule);
        adapter.setQos(topicQos);
        adapter.setOutputChannel(directChannel());
        adapter.setConverter(converter);

        return adapter;
    }

    @Bean
    public MessageChannel directChannel() {
        return new DirectChannel();
    }

    @Bean
    @ServiceActivator(inputChannel = "directChannel")
    public MessageHandler inputHandler() {
        return message -> {

            AbstractRestaurantMessage restaurantMessage = (AbstractRestaurantMessage) message
                .getPayload();
            Long restaurantId = restaurantMessage.getRestaurantId();

            if (message.getPayload() instanceof KeepaliveMessage) {

                treatConnectionMessage(restaurantId);
            } else if (message.getPayload() instanceof ScheduleMessage) {

                treatScheduleMessage(message, restaurantId);
            }
        };
    }

    private void treatScheduleMessage(Message<?> message, Long restaurantId) {
        ScheduleMessage msg = (ScheduleMessage) message.getPayload();

        StatusEntity statusEntity = newUnavailabilityStatusEntity(restaurantId,
            msg.getDtStarts(), msg.getDtEnds());

        statusRepository.save(statusEntity);
    }

    private void treatConnectionMessage(Long restaurantId) {
        String statusCacheKey = CacheUtil.buildStatusCacheKey(restaurantId);

        StatusEntity statusEntity = onlineStatusCache.get(statusCacheKey);
        if (statusEntity != null) {

            // atualiza a data do status que está no cache
            statusEntity.setDtEnds(Instant.now().truncatedTo(ChronoUnit.SECONDS)
                .plus(offlineThreshold, SECONDS));

        } else {

            statusEntity = newOnlineStatusEntity(restaurantId);
        }

        addOrReplaceStatusEntityInCache(onlineStatusCache, statusEntity);

        statusRepository.save(statusEntity);
    }

    private void addOrReplaceStatusEntityInCache(IgniteCache<String, StatusEntity> cache,
        StatusEntity statusEntity) {
        String restKey = CacheUtil.buildStatusCacheKey(statusEntity.getRestaurantId());
        cache
            .withExpiryPolicy(factoryOnlineStatusExpirePolicy.create())
            .put(restKey, statusEntity);
    }

    private StatusEntity newOnlineStatusEntity(Long restaurantId) {
        Instant startsAt = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Instant endsAt = startsAt.plus(offlineThreshold, SECONDS);

        return StatusEntity.builder()
            .id(UUID.randomUUID())
            .restaurantId(restaurantId)
            .dtInits(startsAt)
            .dtEnds(endsAt)
            .type(ONLINE)
            .build();
    }

    private StatusEntity newUnavailabilityStatusEntity(Long restaurantId, Instant dtStarts,
        Instant dtEnds) {

        return StatusEntity.builder()
            .id(UUID.randomUUID())
            .restaurantId(restaurantId)
            .dtInits(dtStarts)
            .dtEnds(dtEnds)
            .type(UNAVAILABLE)
            .build();
    }

    private String getBrokerUrl() {
        return brokerHostname + ":" + brokerPort;
    }
}
