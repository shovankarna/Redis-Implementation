package com.ecommerce.order.service;

import com.ecommerce.order.model.Order;
import com.ecommerce.order.repository.OrderRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class OrderProcessingService {

    private final OrderRepository orderRepository;

    private final StringRedisTemplate redisTemplate;

    @Value("${app.redis.streams.order-events-topic}")
    private String streamKey;

    @Value("${app.redis.streams.max-len}")
    private long maxLen;

    public OrderProcessingService(OrderRepository orderRepository, StringRedisTemplate redisTemplate) {
        this.orderRepository = orderRepository;
        this.redisTemplate = redisTemplate;
    }

    @Transactional
    public Order processOrder(Order orderRequest) {
        // 1. Fully coded relational DB commit step
        orderRequest.setOrderNumber(UUID.randomUUID().toString());
        orderRequest.setCreatedAt(LocalDateTime.now());
        orderRequest.setStatus("PENDING");

        if (orderRequest.getItems() != null) {
            orderRequest.getItems().forEach(item -> item.setOrder(orderRequest));
        }

        Order savedOrder = orderRepository.save(orderRequest);

        // 2. Trigger asynchronous event
        triggerOrderEventStream(savedOrder);

        return savedOrder;
    }

    /**
     * SKELETON ONLY: Redis Streams Producer
     * 
     * Javadocs:
     * Implement Phase 2 Redis Streams Producer here.
     * Use RedisTemplate or StreamOperations to publish the order details to a Redis
     * Stream.
     * 
     * 1. The stream should be named using the
     * `app.redis.streams.order-events-topic` property (e.g.,
     * mystream:order-events).
     * 2. Write structured maps containing order id, total amount, and customer
     * info.
     * 3. Utilize `MAXLEN ~ <length>` (e.g., injected from
     * `app.redis.streams.max-len`) log trimming.
     * Trimming is essential to prevent infinite cluster memory degradation.
     * 
     * @param order The saved Order entity.
     */
    private void triggerOrderEventStream(Order order) {

        // 1. Create a structured map of the event data
        Map<String, String> eventPayLoad = new HashMap<>();
        eventPayLoad.put("orderId", order.getId().toString());
        eventPayLoad.put("orderNumber", order.getOrderNumber());
        eventPayLoad.put("status", order.getStatus());
        eventPayLoad.put("customerId", order.getCustomerId().toString());

        // 2. Build the Stream Record
        MapRecord<String, String, String> record = StreamRecords.newRecord()
                .ofMap(eventPayLoad)
                .withStreamKey(streamKey);

        // 3. Publish to Redis Stream
        redisTemplate.opsForStream().add(record);

        // 4. Manual Log Trimming to prevent memory exhaustion (OOM)
        redisTemplate.opsForStream().trim(streamKey, maxLen);

        System.out.println("Published Order Event to Stream: " + order.getOrderNumber());
    }
}
