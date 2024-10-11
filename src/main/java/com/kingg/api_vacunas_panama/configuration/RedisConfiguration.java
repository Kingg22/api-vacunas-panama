package com.kingg.api_vacunas_panama.configuration;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class RedisConfiguration {

    @Bean
    public RedisConnectionFactory connectionFactory() {
        return new LettuceConnectionFactory();
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer();
        serializer.configure(objectMapper -> objectMapper.registerModule(new JavaTimeModule()));

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);
        return template;
    }

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory factory) {
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer();
        serializer.configure(objectMapper -> objectMapper.registerModule(new JavaTimeModule()));

        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));

        Map<String, RedisCacheConfiguration> configs = new HashMap<>();
        configs.put("tiny", defaultCacheConfig.entryTtl(Duration.ofSeconds(30)));
        configs.put("short", defaultCacheConfig.entryTtl(Duration.ofMinutes(1)));
        configs.put("cache", defaultCacheConfig.entryTtl(Duration.ofMinutes(5)));
        configs.put("medium", defaultCacheConfig.entryTtl(Duration.ofMinutes(30)));
        configs.put("large", defaultCacheConfig.entryTtl(Duration.ofHours(1)));
        configs.put("extraLarge", defaultCacheConfig.entryTtl(Duration.ofHours(2)));
        configs.put("huge", defaultCacheConfig.entryTtl(Duration.ofDays(1)));

        return RedisCacheManager.builder(factory)
                .cacheDefaults(defaultCacheConfig)
                .withInitialCacheConfigurations(configs)
                .build();
    }

}
