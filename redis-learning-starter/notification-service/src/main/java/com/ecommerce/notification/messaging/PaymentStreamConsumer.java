package com.ecommerce.notification.messaging;

import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Service;

@Service
public class PaymentStreamConsumer implements StreamListener<String, MapRecord<String, String, String>> {

    private final StringRedisTemplate redisTemplate;

    @Value("${app.redis.streams.consumer-group}")
    private String consumerGroup;

    public PaymentStreamConsumer(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void onMessage(MapRecord<String, String, String> message) {
        // TODO: Implement Manually
        // 1. Read the event properties
        // 2. Process the event (Simulate notification sending)
        // 3. CRITICAL: Execute Acknowledgment (XACK)
    }
}
