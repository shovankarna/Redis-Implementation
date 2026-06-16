package com.ecommerce.catalog.controller;

import com.ecommerce.catalog.exception.RateLimitExceededException;
import com.ecommerce.catalog.model.Product;
import com.ecommerce.catalog.service.ProductCatalogService;
import com.ecommerce.catalog.service.RateLimiterService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductCatalogService catalogService;
    private final RateLimiterService rateLimiterService;

    @Value("${app.ratelimit.capacity}")
    private int rateLimitCapacity;

    @Value("${app.ratelimit.refill-rate}")
    private int rateLimitRefillRate;

    public ProductController(ProductCatalogService catalogService, RateLimiterService rateLimiterService) {
        this.catalogService = catalogService;
        this.rateLimiterService = rateLimiterService;
    }

    @GetMapping("/{sku}")
    public ResponseEntity<Product> getProduct(@PathVariable String sku, HttpServletRequest request) {
        String clientIp = request.getRemoteAddr();

        // Enforce rate limiting using the Lua Token Bucket skeleton
        boolean allowed = rateLimiterService.isRequestAllowed(clientIp, rateLimitCapacity, rateLimitRefillRate);
        if (!allowed) {
            throw new RateLimitExceededException("Too many requests from IP: " + clientIp);
        }

        // Fetch using the Cache-Aside wrapper skeleton
        Product product = catalogService.getProductWithCache(sku);
        return ResponseEntity.ok(product);
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product created = catalogService.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
