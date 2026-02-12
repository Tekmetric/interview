package com.interview.controller;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@TestConfiguration
@EnableMethodSecurity(prePostEnabled = false)
public class TestSecurityConfig {
}
