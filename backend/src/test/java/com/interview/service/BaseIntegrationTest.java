package com.interview.service;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.interview.config.CacheConfig;
import com.interview.repository.CustomerRepository;

abstract class BaseIntegrationTest {

    @EnableCaching(proxyTargetClass = true)
    @Import(CacheConfig.class)
    static class Config {
    }

    @MockitoBean
    protected CustomerRepository customerRepository;

    @Autowired
    protected CacheManager cacheManager;

    @AfterEach
    void clearAllCaches() {
        cacheManager.getCacheNames().forEach(name -> cacheManager.getCache(name).clear());
    }
}
