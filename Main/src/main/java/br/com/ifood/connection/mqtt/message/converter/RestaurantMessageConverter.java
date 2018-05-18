package br.com.ifood.connection.mqtt.message.converter;

import br.com.ifood.connection.mqtt.message.KeepaliveMessage;
import br.com.ifood.connection.mqtt.message.MqttRestaurantMessage;
import br.com.ifood.connection.mqtt.message.ScheduleMessage;
import java.nio.ByteBuffer;
import java.time.Instant;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.integration.mqtt.support.MqttMessageConverter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
/**
 * Converts an MqttMessage to a Message<MqttRestaurantMessage>.<br/>
 * The convertion logic is taken care in the method
 * {@link MqttMessageConverter#toMessage(String, MqttMessage)}.<br/>
 * The method toMessage from {@link DefaultPahoMessageConverter} is called and after that the
 * payload is converted to the correct {@link MqttRestaurantMessage}.<br/>
 * The logic for {@link org.springframework.messaging.converter.MessageConverter} are the ones
 * defined in {@link DefaultPahoMessageConverter}.
 */
public class RestaurantMessageConverter implements MqttMessageConverter {

    @Value("${mqtt.topic.connection}")
    private String topicConnection;

    @Value("${mqtt.topic.schedule}")
    private String topicSchedule;

    @Value("${mqtt.topic.unschedule}")
    private String topicUnschedule;

    @Value("${mqtt.topic.lastwill}")
    private String topicLastWill;

    @Value("${mqtt.topic.qos}")
    private int topicQos;

    private DefaultPahoMessageConverter defaultConverter = initConverter();

    private DefaultPahoMessageConverter initConverter() {
        DefaultPahoMessageConverter converter = new DefaultPahoMessageConverter(topicQos, false);
        converter.setPayloadAsBytes(true);

        return converter;
    }

    @Override
    public MqttMessage fromMessage(Message<?> message, Class<?> targetClass) {
        return defaultConverter.fromMessage(message, targetClass);
    }

    @Override
    public Message<?> toMessage(Object payload, MessageHeaders headers) {
        return defaultConverter.toMessage(payload, headers);
    }

    @Override
    public Message<MqttRestaurantMessage> toMessage(String topic, MqttMessage mqttMessage) {
        Message<?> originalMessage = defaultConverter.toMessage(topic, mqttMessage);

        PayloadConverter payloadConverter = getPayloadConverter(topic);

        return MessageBuilder.createMessage(
            payloadConverter.convert(mqttMessage.getPayload()),
            originalMessage.getHeaders()
        );
    }

    /**
     * Based on the topic, creates the correct PayloadConverter
     */
    private PayloadConverter getPayloadConverter(String topic) {

        Assert.notNull(topic, "The topic must not be null");
        Assert.isTrue(!topic.isEmpty(), "The topic must not be empty");

        if (topicConnection.equals(topic)) {
            return payload -> {
                ByteBuffer byteBuffer = ByteBuffer.wrap(payload);
                long restaurantId = byteBuffer.getLong();

                return new KeepaliveMessage(restaurantId);
            };
        } else if (topicSchedule.equals(topic)) {
            return payload -> {
                ByteBuffer byteBuffer = ByteBuffer.wrap(payload);
                long restaurantId = byteBuffer.getLong();
                Instant dtStarts = Instant.ofEpochMilli(byteBuffer.getLong());
                Instant dtEnds = Instant.ofEpochMilli(byteBuffer.getLong());

                return new ScheduleMessage(restaurantId, dtStarts, dtEnds);
            };
        }

        throw new IllegalArgumentException(String.format("The topic '%s' is invalid", topic));
    }
}
