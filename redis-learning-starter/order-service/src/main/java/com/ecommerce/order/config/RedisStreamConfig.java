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

        // TODO: Implement Manually
        // 1 & 2: Create Consumer Group (and the stream itself if it doesn't exist).
        // The 'true' boolean at the end represents MKSTREAM
        
        // 3: Set up polling options
        
        // 4: Create and return the container.
        return null;

    }

    @Bean
    public Subscription subscription(
            StreamMessageListenerContainer<String, MapRecord<String, String, String>> container,
            OrderEventConsumer consumer) { // <--- Spring injects your Consumer class here!
        
        // TODO: Implement Manually
        // Subscribe the container to read from the stream, using the Consumer Group.
        // Start the background polling thread
        
        return null;
    }
}
