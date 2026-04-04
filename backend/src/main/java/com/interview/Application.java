package com.interview;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static void main(String[] args) throws Exception {
        // Ensure /tmp/logs exists for log file output on any machine.
        java.nio.file.Path logsDir = java.nio.file.Paths.get("/tmp/logs");
        java.nio.file.Files.createDirectories(logsDir);

        SpringApplication.run(Application.class, args);
    }
}
