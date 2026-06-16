package com.ecommerce.order.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStreamCommands;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.Subscription;

import com.ecommerce.order.messaging.OrderEventConsumer;

import java.time.Duration;

@Configuration
public class RedisStreamConfig {

    @Value("${app.redis.streams.order-events-topic}")
    private String streamKey;

    @Value("${app.redis.streams.consumer-group}")
    private String consumerGroup;

    /**
     * SKELETON ONLY: StreamMessageListenerContainer Setup.
     * 
     * Javadocs:
     * Set up the structural framework for listening to Redis Streams
     * asynchronously.
     * 
     * 1. You must programmatically check if the stream and consumer group exist.
     * 2. Execute `XGROUP CREATE ... MKSTREAM` on startup to initialize a consumer
     * group
     * named `order-processing-group` (using the injected property) if it does not
     * already exist.
     * 3. Configure the
     * `StreamMessageListenerContainer.StreamMessageListenerContainerOptions` to set
     * polling timeouts and batch sizes.
     * 4. Return the configured StreamMessageListenerContainer.
     * 
     * @param connectionFactory the Redis connection factory
     * @return null until implemented manually
     */
    @Bean
    public StreamMessageListenerContainer<String, MapRecord<String, String, String>> streamMessageListenerContainer(
            RedisConnectionFactory connectionFactory) {

        // 1 & 2: Create Consumer Group (and the stream itself if it doesn't exist).
        // The 'true' boolean at the end represents MKSTREAM
        try {
            connectionFactory.getConnection()
                    .streamCommands()
                    .xGroupCreate(
                            streamKey.getBytes(),
                            consumerGroup,
                            ReadOffset.from("0-0"),
                            true);
        } catch (Exception e) {
            // It's normal for this to throw an exception if the group already exists on
            // startup.
            // Safe to ignore.
        }

        // 3: Set up polling options
        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, MapRecord<String, String, String>> options = StreamMessageListenerContainer.StreamMessageListenerContainerOptions
                .builder()
                .pollTimeout(Duration.ofMillis(100)) // How long to block waiting for a new message
                .build();

        // 4: Create and return the container.
        return StreamMessageListenerContainer.create(connectionFactory, options);

    }

    @Bean
    public Subscription subscription(
            StreamMessageListenerContainer<String, MapRecord<String, String, String>> container,
            OrderEventConsumer consumer) { // <--- Spring injects your Consumer class here!
        // Subscribe the container to read from the stream, using the Consumer Group.
        // ReadOffset.lastConsumed() fetches NEW messages never delivered to this group
        // before.
        Subscription subscription = container.receive(
                Consumer.from(consumerGroup, "order-worker-1"),
                StreamOffset.create(streamKey, ReadOffset.lastConsumed()),
                consumer); // <--- We hand your Consumer class to the Container!
        // Start the background polling thread
        container.start(); // <--- Turns on the engine
        return subscription;
    }
}
