package com.interview;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class MovieApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(MovieApplication.class, args);
    }
}
