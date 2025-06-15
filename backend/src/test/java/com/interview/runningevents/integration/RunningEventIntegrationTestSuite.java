package com.interview.runningevents.integration;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

/**
 * This test suite runs all integration tests for the Running Event Management System.
 * It includes controller tests, service tests, repository tests, and database tests.
 */
@Suite
@SelectPackages("com.interview.runningevents.integration")
public class RunningEventIntegrationTestSuite {
    // This class doesn't need any code as it simply serves as a container
    // to run all tests in the specified package
}
