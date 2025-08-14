package com.interview.runner;

import com.interview.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CacheWarmUpRunner implements CommandLineRunner {

    private final CustomerService customerService;
    private final CacheManager cacheManager;

    @Override
    public void run(String... args) {
        System.out.println("Clearing 'customers' cache on startup...");

        if (cacheManager.getCache("customers") != null) {
            cacheManager.getCache("customers").clear();
        }

        System.out.println("Warming up 'customers' cache on startup...");
        customerService.getCustomers("lastName", 0, 3);
    }
}
