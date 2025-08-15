package com.interview.aop;

import org.aspectj.lang.JoinPoint;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Aspect
@Slf4j
@Component
public class CustomerCacheEvictAspect {

    private final CacheManager cacheManager;

    @Autowired
    public CustomerCacheEvictAspect(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    // Pointcut expression intercepting create, update, delete methods
    @AfterReturning("execution(* com.interview..*.createCustomer(..)) || " +
            "execution(* com.interview..*.updateCustomer(..)) || " +
            "execution(* com.interview..*.deleteCustomer(..))")
    public void evictCustomerCache(JoinPoint joinPoint) {
        Cache cache = cacheManager.getCache("customers");
        if (cache != null) {
            cache.clear();
            log.info("Evicted all entries from 'customers' cache after method: {}", joinPoint.getSignature());
        }
    }
}
