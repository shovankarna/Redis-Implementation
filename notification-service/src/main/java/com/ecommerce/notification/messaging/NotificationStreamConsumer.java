package com.ecommerce.notification.messaging;

import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationStreamConsumer implements StreamListener<String, MapRecord<String, String, String>> {

    private final StringRedisTemplate redisTemplate;

    @Value("${app.redis.streams.consumer-group}")
    private String consumerGroup;

    public NotificationStreamConsumer(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void onMessage(MapRecord<String, String, String> message) {
        String streamKey = message.getStream();
        String recordId = message.getId().getValue();
        Map<String, String> payload = message.getValue();

        System.out.println("📥 NOTIFICATION STREAM RECEIVE: Picked up Order Event from Stream: " + payload.get("orderNumber"));

        try {
            // Simulated processing delay for Notification
            Thread.sleep(1000);

            // Acknowledge (XACK) so it's removed from PEL for THIS consumer group
            redisTemplate.opsForStream().acknowledge(streamKey, consumerGroup, recordId);

            System.out.println("✅ NOTIFICATION STREAM XACK: Successfully acknowledged order: " + payload.get("orderNumber"));
        } catch (Exception e) {
            System.err.println("Failed to process stream event: " + e.getMessage());
        }
    }
}
