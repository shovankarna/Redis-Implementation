package com.ecommerce.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "catalog-service", url = "http://localhost:8081")
public interface CatalogClient {

    @GetMapping("/api/products/{sku}")
    Map<String, Object> getProduct(@PathVariable("sku") String sku);
}
