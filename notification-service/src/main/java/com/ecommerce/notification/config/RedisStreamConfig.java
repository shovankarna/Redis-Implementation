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

import java.time.Duration;

@Configuration
public class RedisStreamConfig {

    @Value("${app.redis.streams.order-events-topic}")
    private String streamKey;

    @Value("${app.redis.streams.consumer-group}")
    private String consumerGroup;

    @Bean
    public StreamMessageListenerContainer<String, MapRecord<String, String, String>> streamMessageListenerContainer(
            RedisConnectionFactory connectionFactory) {

        try {
            connectionFactory.getConnection()
                    .streamCommands()
                    .xGroupCreate(
                            streamKey.getBytes(),
                            consumerGroup,
                            ReadOffset.from("0-0"),
                            true);
        } catch (Exception e) {
            // Safe to ignore if group exists
        }

        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, MapRecord<String, String, String>> options = 
                StreamMessageListenerContainer.StreamMessageListenerContainerOptions
                .builder()
                .pollTimeout(Duration.ofMillis(100))
                .build();

        return StreamMessageListenerContainer.create(connectionFactory, options);
    }

    @Bean
    public Subscription streamSubscription(
            StreamMessageListenerContainer<String, MapRecord<String, String, String>> container,
            NotificationStreamConsumer consumer) {
        
        Subscription subscription = container.receive(
                Consumer.from(consumerGroup, "notification-worker-1"),
                StreamOffset.create(streamKey, ReadOffset.lastConsumed()),
                consumer);
        container.start();
        return subscription;
    }
}
