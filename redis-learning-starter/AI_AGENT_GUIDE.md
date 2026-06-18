# Enterprise E-Commerce Engine: AI Agent Guide

Welcome, AI Agent! This document explains the architecture, structure, and intent behind this repository.

## Purpose
This project is a **Master-Tier Learning Playground for Redis**, constructed as an Enterprise E-Commerce Engine. It consists of three Spring Boot 3.x microservices: `catalog-service`, `order-service`, and `notification-service`.

**IMPORTANT FOR AI AGENTS**: 
The repository intentionally contains **incomplete** code regarding Redis operations. 
The Spring Boot boilerplate, JPA entities, database setup, controllers, and exception handlers are fully implemented.
However, **all Redis-related configurations, cache operations, stream producers/consumers, pub/sub logic, and Lua scripts are left as skeletons**. 
These skeletons contain detailed Javadocs with instructions and a `// TODO: Implement Manually` marker. This is meant for a human user to practice implementing Redis patterns (Cache-Aside, Rate Limiting, Streams, Pub/Sub) manually. 
**Do not auto-complete these TODOs unless explicitly requested by the user.**

## Architecture
1. **Catalog-Service**: Read-heavy domain for product inventories. Uses PostgreSQL (`catalog_db`). Targeted Redis concepts: Caching, Lettuce Connection Pooling, TTLs, Cache-Aside pattern, Cache Penetration/Avalanche mitigation, Token Bucket Rate Limiting (Lua).
2. **Order-Service**: Write-heavy domain for processing orders. Uses PostgreSQL (`order_db`). Targeted Redis concepts: Redis Streams (Producer/Consumer), Consumer Groups, XACK, PEL (Pending Entries List), `MAXLEN` log trimming.
3. **Notification-Service**: Real-time worker for alerts. Targeted Redis concepts: Redis Pub/Sub, `ChannelTopic`, Fire-and-forget broadcasting. **Also demonstrates Redis Streams Fan-Out** by consuming from multiple streams (`order-events`, `payment-events`, `user-events`) via an independent consumer group.

## Infrastructure
The system uses Docker Compose to provision:
- An isolated network bridge (`ecommerce-mesh`).
- PostgreSQL with logically separated DBs.
- Redis with specific production-grade flags (`--appendonly yes --maxmemory 768mb --maxmemory-policy allkeys-lru`).

## Running the Application
1. Start infrastructure: `docker-compose up -d`
2. Build the project: `mvn clean install`
3. Run each service (they are mapped to ports 8081, 8082, 8083 respectively).
