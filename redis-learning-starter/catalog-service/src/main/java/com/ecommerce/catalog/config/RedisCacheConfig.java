package com.ecommerce.catalog.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

@Configuration
@EnableCaching
public class RedisCacheConfig {

    @Value("${app.cache.product.ttl-minutes}")
    private long defaultTtlMinutes;

    @Value("${app.cache.product.null-value-ttl-minutes}")
    private long nullValueTtlMinutes;

    /**
     * SKELETON ONLY: Configure RedisCacheManager.
     * 
     * Javadocs:
     * Use RedisCacheManagerBuilder to construct a robust CacheManager.
     * 
     * 1. Set explicit separate TTLs per cache name (e.g., specific TTL for
     * "products" cache).
     * 2. Track metrics/statistics: Enable statistics so Micrometer/Actuator can
     * monitor hit/miss ratios.
     * 3. Enable null-value caching (and configure an explicit short TTL for nulls
     * like 5 minutes)
     * to mitigate Cache Penetration (when users query for non-existent products
     * maliciously).
     * 
     * Utilize the injected properties from application.yml for TTL values.
     * 
     * @param connectionFactory the Redis connection factory
     * @return null until implemented manually
     */
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {

        // TODO: Implement Manually
        // 1. Create a default RedisCacheConfiguration
        // 2. Create custom configurations for specific caches (e.g., long-lived vs short-lived)
        // 3. Build the RedisCacheManager
        
        return null;

    }
}
