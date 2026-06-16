package com.ecommerce.catalog.service;

import com.ecommerce.catalog.exception.ResourceNotFoundException;
import com.ecommerce.catalog.model.Product;
import com.ecommerce.catalog.repository.ProductRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class ProductCatalogService {

    private final ProductRepository productRepository;

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${app.cache.product.ttl-minutes}")
    private long baseTtlMinutes;

    @Value("${app.cache.product.jitter-seconds}")
    private long jitterSeconds;

    public ProductCatalogService(ProductRepository productRepository, RedisTemplate<String, Object> redisTemplate) {
        this.productRepository = productRepository;
        this.redisTemplate = redisTemplate;
    }

    /**
     * Standard JPA query to find product by SKU.
     */
    public Product getProductBySkuFromDb(String sku) {
        return productRepository.findBySku(sku)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with SKU: " + sku));
    }

    /**
     * Implement a manual Cache-Aside strategy for fetching products.
     * It gives more control over annotation
     * 1. Attempt to read from Redis using RedisTemplate.
     * 2. If present, return the cached Product immediately.
     * 3. If absent, fallback to calling `getProductBySkuFromDb(sku)`.
     * 4. Write the fetched item back to Redis.
     * 
     * IMPORTANT: Cache Avalanche and Cache Penetration mitigations:
     * - Inject random TTL Jitter (using the jitterSeconds property) on top of
     * baseTtlMinutes
     * to prevent a Cache Avalanche (where many keys expire at the exact same
     * microsecond, slamming the DB).
     * - Write sentinel "NULL" tokens for missing items to prevent Cache
     * Penetration.
     * 
     * @param sku The product SKU.
     * @return Product entity.
     */
    public Product getProductWithCache(String sku) {

        // 1. Build the cache key
        String cacheKey = "product::" + sku;

        // 2. Try to fetch from cache
        Object cachedValue = redisTemplate.opsForValue().get(cacheKey);

        if (cachedValue != null) {

            // Check for our "Null Sentinel" to mitigate Cache Penetration
            if (cachedValue.equals("NULL_SENTINEL")) {
                throw new ResourceNotFoundException("Product not found with SKU: " + sku);
            }

            if (cachedValue instanceof Product) {
                return (Product) cachedValue;
            } else {
                throw new ResourceNotFoundException("Product not found with SKU: " + sku);
            }
        }

        // 3. Cache Miss - fetch from DB
        Product product;
        try {
            product = getProductBySkuFromDb(sku);

            // Apply jitter to TTL to prevent cache avalanche
            long jitter = ThreadLocalRandom.current().nextLong(0, jitterSeconds + 1);
            Duration ttl = Duration.ofMinutes(baseTtlMinutes).plusSeconds(jitter);

            // Write to Redis with TTL
            redisTemplate.opsForValue().set(cacheKey, product, ttl);

        } catch (Exception e) {
            // Mitigate Cache Penetration: Hacker is querying a fake SKU.
            // Cache a "NULL_SENTINEL" for 5 minutes so they don't spam the DB.

            redisTemplate.opsForValue().set(cacheKey, "NULL_SENTINEL", Duration.ofMinutes(5));
            throw e;
        }

        return product;
    }

    /**
     * 1. THE LOOKUP (@Cacheable)
     * 
     * This fully replaces the manual getProductWithCache() method.
     * Its the same Cache-Aside Strategy as getProductWithCache() method.
     * But here we use annotations to achieve the same result.
     * 
     * Features used here:
     * - value = "products": Maps to the "products" cache config in
     * RedisCacheManager.
     * - key = "#sku": Uses Spring Expression Language (SpEL) to grab the parameter.
     * Key becomes "products::123".
     * - sync = true: Mitigates "Cache Stampede". If 100 threads miss the cache
     * simultaneously,
     * only ONE thread queries the DB while the others wait for it to populate the
     * cache.
     * - condition: Only caches if the SKU is at least 3 characters long.
     * - unless: Prevents caching if the result from the DB is null (Note: removing
     * this allows
     * null-caching to prevent Cache Penetration!).
     */

    @Cacheable(value = "products", key = "#sku", sync = true, condition = "#sku.length() >= 3", unless = "#result == null")
    public Product getProductWithAnnotationCache(String sku) {
        // Look how clean the business logic is!
        // Spring's proxy intercepts the call, checks Redis, and only runs this if it's
        // a Cache Miss.
        return getProductBySkuFromDb(sku);
    }

    /**
     * 2. THE UPDATE (@CachePut)
     * 
     * @CachePut always executes the method, but then takes the RETURNED object
     *           and jams it into the Redis Cache under the specified key.
     *           Use this when a user edits/updates a product so the cache stays
     *           fresh.
     */

    @CachePut(value = "products", key = "#product.sku")
    public Product updateProductWithAnnotationCache(Product product) {
        // Save to DB
        return productRepository.save(product);
        // Spring intercepts the returned Product and updates Redis automatically!
    }

    /**
     * 3. THE DELETE (@CacheEvict)
     * 
     * @CacheEvict removes the key from Redis.
     *             Use this when an item is deleted from the DB so you don't serve
     *             stale, deleted data.
     */

    @CacheEvict(value = "products", key = "#sku")
    @Transactional
    public void deleteProductWithAnnotationCache(String sku) {
        Product product = getProductBySkuFromDb(sku);
        productRepository.delete(product);
        // Spring intercepts this and runs: DEL products::sku
    }

    /**
     * Standard JPA creation logic.
     */
    @Transactional
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }
}
