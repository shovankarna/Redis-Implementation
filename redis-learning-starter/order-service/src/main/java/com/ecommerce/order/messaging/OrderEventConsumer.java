package com.ecommerce.order.messaging;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Service;

@Service
public class OrderEventConsumer implements StreamListener<String, MapRecord<String, String, String>> {

    private final StringRedisTemplate redisTemplate;

    @Value("${app.redis.streams.consumer-group}")
    private String consumerGroup;

    public OrderEventConsumer(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * SKELETON ONLY: Redis Stream Consumer callback.
     * 
     * Javadocs:
     * This method is triggered whenever a new order event is received in the
     * consumer group.
     * 
     * 1. Read the event properties (e.g., order ID, product SKUs).
     * 2. Process local inventory deductions (simulated or by calling the Catalog
     * Service).
     * 3. Handle processing failures gracefully (catch exceptions).
     * 4. CRITICAL: Execute an explicit Acknowledgment (`XACK`) to the stream once
     * processing succeeds.
     * Why XACK? If a consumer crashes before acknowledging, the message remains in
     * the
     * Pending Entries List (PEL) so that it can be claimed by another consumer
     * later.
     * 
     * @param message the stream record
     */
    @Override
    public void onMessage(MapRecord<String, String, String> message) {

        // TODO: Implement Manually
        // 1. Read the event properties
        // 2. Process the event (Simulate inventory deduction, payment processing, etc.)
        // 3. CRITICAL: Execute Acknowledgment (XACK)
    }
}
