package com.interview.runner;

import com.interview.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CacheWarmUpRunner implements CommandLineRunner {

    private final CustomerService customerService;

    @Override
    public void run(String... args) {
        System.out.println("Warming up 'customers' cache on startup...");
        customerService.getCustomers("lastName", 0, 3);
    }
}