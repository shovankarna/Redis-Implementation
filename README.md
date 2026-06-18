# Enterprise E-Commerce Engine: Advanced Redis & Spring Boot Architecture

Welcome to the **Enterprise E-Commerce Engine**, a master-tier microservices playground designed to demonstrate and implement advanced, production-grade **Redis** patterns alongside **Spring Boot 3.x** and **PostgreSQL**.

This project moves beyond simple caching to explore event-driven architectures, distributed rate limiting, and highly scalable message brokering.

## 🏗 Architecture Overview

The system is composed of three logically isolated, domain-driven microservices interacting over an isolated Docker Compose mesh network.

### 1. Catalog Service (`:8081`)
A read-heavy domain for managing product inventories.
* **Database:** PostgreSQL (`catalog_db`)
* **Key Redis Patterns Implemented:**
  * **Cache-Aside Pattern:** Manual and annotation-driven (`@Cacheable`, `@CachePut`, `@CacheEvict`) strategies to reduce database load.
  * **Cache Penetration & Avalanche Mitigation:** Implementation of NULL-sentinels and jittered Time-To-Live (TTL) strategies.
  * **Distributed Rate Limiting:** Lua-script backed Token Bucket algorithm to protect endpoints from spam/DDoS. **Not Implemented Yet!**

### 2. Order Service (`:8082`)
A write-heavy domain for processing customer orders asynchronously.
* **Database:** PostgreSQL (`order_db`)
* **Key Redis Patterns Implemented:**
  * **Event Sourcing with Redis Streams:** Acts as the primary message broker. Emits `order-events` via `MapRecord`.
  * **Asynchronous Processing:** Non-blocking producer execution.
  * **Reliable Delivery:** Utilizes Consumer Groups, explicit Acknowledgments (`XACK`), and Pending Entries Lists (PEL) to guarantee at-least-once processing.
  * **Log Trimming:** Automated `MAXLEN` log trimming to prevent memory exhaustion (OOM).

### 3. Notification Service (`:8083`)
A central communications hub designed to react to real-time events across the ecosystem.
* **Key Redis Patterns Implemented:**
  * **Real-time Pub/Sub:** Fire-and-forget broadcasting for transient alerts (`live-alerts` channel).
  * **Stream Fan-Out:** Acts as an independent consumer group reading from multiple enterprise streams (e.g., `order-events`, `payment-events`, `user-events`) simultaneously without blocking the primary business processors.

## Infrastructure & Setup

The entire environment is orchestrated via Docker Compose, ensuring zero local dependency headaches.

### Prerequisites
* Java 17+
* Maven 3.8+
* Docker & Docker Compose

### Getting Started

1. **Boot up the Infrastructure:**
   Spin up PostgreSQL and Redis (with optimized production flags) in the background.
   ```bash
   docker-compose up -d
   ```

2. **Build the Microservices:**
   ```bash
   mvn clean install
   ```

3. **Run the Services:**
   Start the Spring Boot applications individually via your IDE or terminal.
   * Catalog Service (`8081`)
   * Order Service (`8082`)
   * Notification Service (`8083`)

## 🛠 Testing the Architecture

You can test the complex, asynchronous flows using simple REST calls and the Redis CLI.

**Test Caching & Rate Limiting (Catalog Service):**
```bash
curl -X POST http://localhost:8081/api/products -H "Content-Type: application/json" -d '{"sku":"PROD1", "name":"Laptop", "price":999.99, "stockQuantity":10}'

# Run multiple times to observe Cache Hits and trigger Rate Limiting
curl http://localhost:8081/api/products/PROD1
```

**Test Event Streams & Fan-out (Order -> Notification):**
```bash
# Place an order. Watch both the Order Service and Notification Service consoles instantly pick up the event!
curl -X POST http://localhost:8082/api/orders -H "Content-Type: application/json" -d '{"orderNumber":"ORD-001", "customerId":"CUST-1", "totalAmount":999.99, "status":"PENDING"}'
```

**Test Transient Pub/Sub (Notification Service):**
```bash
docker exec -it <YOUR_REDIS_CONTAINER> redis-cli PUBLISH live-alerts "System Online"
```

## Educational Goals
This repository serves as a practical implementation guide for:
* Distinguishing between when to use **Redis Streams** (persistent, reliable, consumer-groups) vs **Redis Pub/Sub** (fire-and-forget, transient).
* Understanding Connection Pooling via **Lettuce**.
* Safeguarding distributed systems from cascading failures using caches and rate limiters.
