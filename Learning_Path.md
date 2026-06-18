# Enterprise Redis Microservices Implementation Roadmap

This document outlines the step-by-step flow to implement and test the complete Redis architecture across all three microservices. We will follow a strict "Implement -> Test -> Next Phase" methodology.

## Phase 1: Catalog Service (Caching & Rate Limiting)
**Focus:** Connection Pooling, Cache-Aside Pattern, TTLs, and Lua Scripting.

1.  **[COMPLETED]** `RedisEnterpriseConfig.java`: Configure Lettuce connection pooling and JSON serializers.
2.  **[COMPLETED]** `RedisCacheConfig.java`: Configure Spring's `RedisCacheManager` with TTLs to prevent memory exhaustion and enable statistics.
3.  **[COMPLETED]** `ProductCatalogService.java`: Implement the Cache-Aside pattern manually using `@Cacheable`, `@CachePut`, or `RedisTemplate` directly.
4.  **[SKIPPED]** `RateLimiterService.java`: Implement a Token Bucket rate limiter using a Redis Lua script to protect the API from spam.
5.  **TESTING PHASE 1**: **[COMPLETED]**
    *   Start Docker Compose (PostgreSQL & Redis).
    *   Run `catalog-service`.
    *   Hit the API to verify products are cached in Redis.
    *   Test rate-limiting by spamming the endpoint.

---

## Phase 2: Order Service (Event Sourcing with Redis Streams)
**Focus:** Asynchronous event processing, Producer/Consumer, Consumer Groups, and At-Least-Once Delivery.

1.  **[COMPLETED]** `RedisStreamConfig.java`: Configure the `StreamMessageListenerContainer` and programmatically create the `order-processing-group` consumer group.
2.  **[COMPLETED]** `OrderProcessingService.java`: Implement the Producer to push new order events (as `MapRecord`) to the Redis Stream.
3.  **[COMPLETED]** `OrderEventConsumer.java`: Implement the Consumer to listen for stream events, process them, and crucially, acknowledge (`XACK`) the messages.
4.  **TESTING PHASE 2**: **[COMPLETED]**
    *   Run `order-service` alongside `catalog-service`.
    *   Submit an order via the API.
    *   Verify the event is written to the stream and successfully consumed and acknowledged by the worker.

---

## Phase 3: Notification Service (Real-time Pub/Sub)
**Focus:** Fire-and-forget broadcasting, Channels, and decoupled messaging.

1.  **[COMPLETED]** `RedisPubSubConfig.java`: Configure a `RedisMessageListenerContainer` and attach a `MessageListenerAdapter` to the `live-alerts` channel.
2.  **[COMPLETED]** `NotificationPublisher.java`: Implement the logic to broadcast messages to the Redis Pub/Sub channel.
3.  **[COMPLETED]** `NotificationSubscriber.java`: Implement the listener that receives the broadcast and sends the "alert".
4.  **TESTING PHASE 3**: **[COMPLETED]**
    *   Run `notification-service`.
    *   Trigger a system event that publishes a message.
    *   Verify the notification service instantly receives the broadcast.

---

## Phase 3.5: Notification Service (Streams Fan-Out)
**Focus:** Multiple Consumer Groups, Stream Fan-Out, and Independent Event Processing.

1.  **[COMPLETED]** `RedisStreamConfig.java`: Configure the `StreamMessageListenerContainer` to listen to multiple streams (`order-events`, `payment-events`, `user-events`) using a distinct consumer group (`notification-processing-group`).
2.  **[COMPLETED]** `NotificationStreamConsumer.java`: Implement the consumer for order events.
3.  **[COMPLETED]** `PaymentStreamConsumer.java`: Implement the hypothetical consumer for payment events.
4.  **[COMPLETED]** `UserStreamConsumer.java`: Implement the hypothetical consumer for user events.
5.  **TESTING PHASE 3.5**: **[COMPLETED]**
    *   Trigger stream events using `redis-cli XADD`.
    *   Verify the notification service independently receives and processes these events.

---

## Phase 4: Full System Integration Test **[PENDING]**
**Focus:** End-to-end functionality.

1.  Place an order.
2.  Verify Rate Limiter allows/blocks the request.
3.  Verify Catalog Cache is updated/invalidated if inventory changes.
4.  Verify Order Stream processes the payment/inventory logic asynchronously.
5.  Verify Notification Pub/Sub alerts the user of the successful order.
