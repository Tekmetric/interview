package com.interview.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.interview.config.JpaAuditingConfig;

@DataJpaTest
@Import(JpaAuditingConfig.class)
abstract class BaseIntegrationTest {

    @Autowired
    protected CustomerRepository customerRepository;

    @Autowired
    protected CreditApplicationRepository applicationRepository;
}
