package com.ecommerce.notification.messaging;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationPublisher {

    @Value("${app.redis.pubsub.alerts-channel}")
    private String alertsChannel;

    private final StringRedisTemplate stringRedisTemplate;

    public NotificationPublisher(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * SKELETON ONLY: Broadcast System Alert.
     * 
     * Javadocs:
     * Implement the fire-and-forget push broadcasting using RedisTemplate.
     * 
     * Key concept: Pub/Sub vs Streams
     * In basic Pub/Sub, messages are permanently dropped if a consumer is offline
     * for even a millisecond.
     * This is contrasted against the log-backed longevity of Redis Streams (used in
     * Order-Service),
     * which retains messages until explicitly acknowledged (or trimmed via MAXLEN).
     * Use Pub/Sub for transient events (like UI live-alerts), but NEVER for
     * business-critical
     * transactions (like inventory deduction).
     * 
     * @param message The alert message to broadcast.
     */

    public void broadcastSystemAlert(String message) {
        System.out.println(" PUB/SUB PUBLISH: Broadcasting alert to channel '" + alertsChannel + "': " + message);
        // Publishes the message to the Redis Pub/Sub channel
        stringRedisTemplate.convertAndSend(alertsChannel, message);
    }
}
