package com.ecommerce.notification.messaging;

import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Service;

@Service
public class UserStreamConsumer implements StreamListener<String, MapRecord<String, String, String>> {

    private final StringRedisTemplate redisTemplate;

    @Value("${app.redis.streams.consumer-group}")
    private String consumerGroup;

    public UserStreamConsumer(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void onMessage(MapRecord<String, String, String> message) {
        String streamKey = message.getStream();
        String recordId = message.getId().getValue();
        Map<String, String> payload = message.getValue();

        System.out.println("USER STREAM RECEIVE: Picked up User Event from Stream for User ID: " + payload.get("userId"));

        try {
            // Simulated processing delay for sending a Welcome Email
            Thread.sleep(500);

            // Acknowledge (XACK) so it's removed from PEL
            redisTemplate.opsForStream().acknowledge(streamKey, consumerGroup, recordId);

            System.out.println("USER STREAM XACK: Successfully acknowledged user: " + payload.get("userId"));
        } catch (Exception e) {
            System.err.println("Failed to process user stream event: " + e.getMessage());
        }
    }
}
