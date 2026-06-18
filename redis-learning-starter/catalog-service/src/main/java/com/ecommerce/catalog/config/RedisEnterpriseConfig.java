package com.ecommerce.catalog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisEnterpriseConfig {

    /**
     * SKELETON ONLY: Configure RedisTemplate<String, Object>.
     * 
     * Javadocs:
     * A customized RedisTemplate is essential for overriding default Java
     * serializers.
     * Implement this method to use a StringRedisSerializer for the keys and a
     * GenericJackson2JsonRedisSerializer for the values.
     * 
     * @return null until implemented manually
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {

        // TODO: Implement Manually
        // 1. Instantiate a new RedisTemplate
        // 2. Set the ConnectionFactory
        // 3. Set the Key and HashKey serializers to StringRedisSerializer
        // 4. Set the Value and HashValue serializers to GenericJackson2JsonRedisSerializer
        // 5. Call afterPropertiesSet()
        
        return null;
    }
}
