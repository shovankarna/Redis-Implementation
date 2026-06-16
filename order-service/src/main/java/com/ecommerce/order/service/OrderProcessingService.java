package com.ecommerce.order.service;

import com.ecommerce.order.model.Order;
import com.ecommerce.order.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class OrderProcessingService {

    private final OrderRepository orderRepository;

    public OrderProcessingService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
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
     * Use RedisTemplate or StreamOperations to publish the order details to a Redis Stream.
     * 
     * 1. The stream should be named using the `app.redis.streams.order-events-topic` property (e.g., mystream:order-events).
     * 2. Write structured maps containing order id, total amount, and customer info.
     * 3. Utilize `MAXLEN ~ <length>` (e.g., injected from `app.redis.streams.max-len`) log trimming.
     *    Trimming is essential to prevent infinite cluster memory degradation.
     * 
     * @param order The saved Order entity.
     */
    private void triggerOrderEventStream(Order order) {
        // TODO: Implement Phase 2 Redis Streams Producer
    }
}
