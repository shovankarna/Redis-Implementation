package com.ecommerce.notification.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.Subscription;

import com.ecommerce.notification.messaging.NotificationStreamConsumer;
import com.ecommerce.notification.messaging.PaymentStreamConsumer;
import com.ecommerce.notification.messaging.UserStreamConsumer;

import java.time.Duration;

@Configuration
public class RedisStreamConfig {

    @Value("${app.redis.streams.order-events-topic}")
    private String orderStreamKey;

    @Value("${app.redis.streams.payment-events-topic}")
    private String paymentStreamKey; // Hypothetical payment stream

    @Value("${app.redis.streams.user-events-topic}")
    private String userStreamKey; // Hypothetical user stream

    @Value("${app.redis.streams.consumer-group}")
    private String consumerGroup;

    @Bean
    public StreamMessageListenerContainer<String, MapRecord<String, String, String>> streamMessageListenerContainer(
            RedisConnectionFactory connectionFactory) {

        // 1. You must ensure the Consumer Group is created for EVERY stream you want to
        // listen to.
        createConsumerGroupSafe(connectionFactory, orderStreamKey, consumerGroup);
        createConsumerGroupSafe(connectionFactory, paymentStreamKey, consumerGroup);
        createConsumerGroupSafe(connectionFactory, userStreamKey, consumerGroup);

        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, MapRecord<String, String, String>> options = StreamMessageListenerContainer.StreamMessageListenerContainerOptions
                .builder()
                .pollTimeout(Duration.ofMillis(100))
                .build();

        return StreamMessageListenerContainer.create(connectionFactory, options);
    }

    @Bean
    public Subscription streamSubscription(
            StreamMessageListenerContainer<String, MapRecord<String, String, String>> container,
            NotificationStreamConsumer orderConsumer,
            PaymentStreamConsumer paymentConsumer,
            UserStreamConsumer userConsumer) {

        // 2. We call container.receive() for EVERY stream to hook them up to their
        // specific listener class.

        // Subscribe to Orders
        Subscription orderSub = container.receive(
                Consumer.from(consumerGroup, "notification-worker-1"),
                StreamOffset.create(orderStreamKey, ReadOffset.lastConsumed()),
                orderConsumer);

        // Subscribe to Payments (Hypothetical)
        Subscription paymentSub = container.receive(
                Consumer.from(consumerGroup, "notification-worker-1"),
                StreamOffset.create(paymentStreamKey, ReadOffset.lastConsumed()),
                paymentConsumer);

        // Subscribe to Users (Hypothetical)
        Subscription userSub = container.receive(
                Consumer.from(consumerGroup, "notification-worker-1"),
                StreamOffset.create(userStreamKey, ReadOffset.lastConsumed()),
                userConsumer);

        container.start();
        return orderSub; // Returning one is fine for Spring context, the container manages all of them
    }

    /**
     * Helper method to safely create a consumer group for a given stream key.
     */
    private void createConsumerGroupSafe(RedisConnectionFactory connectionFactory, String streamKey, String groupName) {
        try {
            connectionFactory.getConnection()
                    .streamCommands()
                    .xGroupCreate(
                            streamKey.getBytes(),
                            groupName,
                            ReadOffset.from("0-0"),
                            true); // true = MKSTREAM (create stream if it doesn't exist)
        } catch (Exception e) {
            // Safe to ignore if group already exists
        }
    }
}
