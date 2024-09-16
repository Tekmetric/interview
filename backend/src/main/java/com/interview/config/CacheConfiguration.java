package com.interview.config;

import com.hazelcast.config.*;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.instance.impl.HazelcastInstanceFactory;
import com.hazelcast.spring.cache.HazelcastCacheManager;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
@EnableCaching
public class CacheConfiguration {

    public static final String CACHE_ARTIST_INFO = "artistInformation";

    @Bean
    public KeyGenerator keyGenerator() {
        return new SimpleKeyGenerator();
    }

    @Bean
    public CacheManager cacheManager() {
        return new HazelcastCacheManager(getHazelcastInstance(hazelcastConfig()));
    }

    @Bean(name = "hazelcastInstance")
    public HazelcastInstance getHazelcastInstance(Config config) {
        return HazelcastInstanceFactory.newHazelcastInstance(config);
    }

    @Bean
    public Config hazelcastConfig() {
        var config = new Config();
        config.setInstanceName("vinyl-app-" + UUID.randomUUID())
                .addMapConfig(
                        new MapConfig()
                                .setName(CACHE_ARTIST_INFO)
                                .setEvictionConfig(new EvictionConfig().setEvictionPolicy(EvictionPolicy.LRU).setMaxSizePolicy(MaxSizePolicy.PER_NODE))
                                .setTimeToLiveSeconds(36000)); //10 hours
        return config;
    }

}
