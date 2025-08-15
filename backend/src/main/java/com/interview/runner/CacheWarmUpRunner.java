package com.interview.runner;

import com.interview.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CacheWarmUpRunner implements CommandLineRunner {
    private final CustomerService customerService;
    private final CacheManager cacheManager;

    // Run after the Spring Boot starts up.
    @Override
    public void run(String... args) {
        log.info("Clearing 'customers' cache on startup...");

        if (cacheManager.getCache("customers") != null) {
            cacheManager.getCache("customers").clear();
        }

        log.info("Warming up 'customers' cache on startup...");
        customerService.getCustomers("lastName", 0, 3);
    }
}
