package com.interview.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;


@Configuration
public class RedisCacheConfig {
    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule()) // for Java 8 dates
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            // if not activated, get LinkedHashMap instead of the actual DTO class when deserializing collections or polymorphic fields.
            .activateDefaultTyping(
                    LaissezFaireSubTypeValidator.instance,
                    ObjectMapper.DefaultTyping.NON_FINAL,
                    JsonTypeInfo.As.PROPERTY
            );

        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        return RedisCacheConfiguration.defaultCacheConfig()
            .serializeValuesWith(
                    RedisSerializationContext.SerializationPair.fromSerializer(serializer)
            );
    }
}
