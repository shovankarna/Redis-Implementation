package com.ecommerce.notification.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import com.ecommerce.notification.messaging.NotificationSubscriber;

@Configuration
public class RedisPubSubConfig {

    @Value("${app.redis.pubsub.alerts-channel}")
    private String alertsChannel;

    /**
     * SKELETON ONLY: RedisMessageListenerContainer Setup.
     * 
     * Javadocs:
     * Set up the structural framework for a RedisMessageListenerContainer.
     * 
     * 1. This container requires a RedisConnectionFactory.
     * 2. It must bind a `ChannelTopic` (named via the
     * `app.redis.pubsub.alerts-channel` property, e.g., "live-alerts")
     * to a specific message listener adapter (like your NotificationSubscriber).
     * 
     * @param connectionFactory the Redis connection factory
     * @return null until implemented manually
     */
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory connectionFactory,
            NotificationSubscriber subscriber) {

        // TODO: Implement Manually
        // 1. Create a RedisMessageListenerContainer
        // 2. Set the ConnectionFactory
        // 3. Bind the subscriber to the specific channel topic using addMessageListener()
        
        return null;
    }

    @Bean
    public ChannelTopic channelTopic() {
        return new ChannelTopic(alertsChannel);
    }
}
