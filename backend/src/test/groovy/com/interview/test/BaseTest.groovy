package com.interview.test

import com.interview.Application
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.client.RestClient
import spock.lang.Specification

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, properties = "spring.config.location=classpath:/application-test.yaml")
@ContextConfiguration(classes = Application.class)
@DirtiesContext
class BaseTest extends Specification {

    def setupSpec() {}

    def cleanupSpec() {}

    def setup() {}

    def cleanup() {}

    def restClient(String role="admin") {
        RestClient.builder()
            .baseUrl("http://localhost:8083")
            .requestFactory(new SimpleClientHttpRequestFactory())
            .defaultHeaders { headers -> headers.setBasicAuth(role, "password") }
            .build()
    }
}
