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

        String streamKey = message.getStream();
        String recordId = message.getId().getValue();
        Map<String, String> payload = message.getValue();

        System.out.println("📥 ORDER STREAM RECEIVE: Picked up Order Event from Stream: " + payload.get("orderNumber"));

        try {
            // 1. Process the event (Simulate inventory deduction, payment processing, etc.)
            // If this crashes, the exception is caught and XACK is never sent.
            Thread.sleep(500); // Simulated processing time

            // 2. CRITICAL: Execute Acknowledgment (XACK)
            // This removes the message from the Pending Entries List (PEL) for this
            // consumer group
            redisTemplate.opsForStream().acknowledge(streamKey, consumerGroup, recordId);

            System.out.println("✅ ORDER STREAM XACK: Successfully processed and Acknowledged order: "
                    + payload.get("orderNumber"));
        } catch (Exception e) {
            System.err.println("Failed to process order event: " + e.getMessage());
            // Because we didn't XACK, the message remains in the PEL and can be re-claimed
            // by another worker later!
        }
    }
}
