# Enterprise Redis Microservices Implementation Roadmap

This document outlines the step-by-step flow to implement and test the complete Redis architecture across all three microservices. We will follow a strict "Implement -> Test -> Next Phase" methodology.

## Phase 1: Catalog Service (Caching & Rate Limiting)
**Focus:** Connection Pooling, Cache-Aside Pattern, TTLs, and Lua Scripting.

1.  **[COMPLETED]** `RedisEnterpriseConfig.java`: Configure Lettuce connection pooling and JSON serializers.
2.  **[COMPLETED]** `RedisCacheConfig.java`: Configure Spring's `RedisCacheManager` with TTLs to prevent memory exhaustion and enable statistics.
3.  **[COMPLETED]** `ProductCatalogService.java`: Implement the Cache-Aside pattern manually using `@Cacheable`, `@CachePut`, or `RedisTemplate` directly.
4.  **[SKIPPED]** `RateLimiterService.java`: Implement a Token Bucket rate limiter using a Redis Lua script to protect the API from spam.
5.  **TESTING PHASE 1**: 
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
4.  **TESTING PHASE 2**:
    *   Run `order-service` alongside `catalog-service`.
    *   Submit an order via the API.
    *   Verify the event is written to the stream and successfully consumed and acknowledged by the worker.

---

## Phase 3: Notification Service (Real-time Pub/Sub)
**Focus:** Fire-and-forget broadcasting, Channels, and decoupled messaging.

1.  **[PENDING]** `RedisPubSubConfig.java`: Configure a `RedisMessageListenerContainer` and attach a `MessageListenerAdapter` to the `live-alerts` channel.
2.  **[PENDING]** `NotificationPublisher.java`: Implement the logic to broadcast messages to the Redis Pub/Sub channel.
3.  **[PENDING]** `NotificationSubscriber.java`: Implement the listener that receives the broadcast and sends the "alert".
4.  **TESTING PHASE 3**:
    *   Run `notification-service`.
    *   Trigger a system event that publishes a message.
    *   Verify the notification service instantly receives the broadcast.

---

## Phase 4: Full System Integration Test
**Focus:** End-to-end functionality.

1.  Place an order.
2.  Verify Rate Limiter allows/blocks the request.
3.  Verify Catalog Cache is updated/invalidated if inventory changes.
4.  Verify Order Stream processes the payment/inventory logic asynchronously.
5.  Verify Notification Pub/Sub alerts the user of the successful order.

## User Review Required
Does this step-by-step roadmap align with how you want to learn and progress? Once approved, we will immediately jump into step 2 of Phase 1 (`RedisCacheConfig.java`).
