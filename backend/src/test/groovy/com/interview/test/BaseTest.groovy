package com.interview.test

import com.interview.Application
import com.interview.feature.project.ProjectRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.client.RestClient
import spock.lang.Specification

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, properties = "spring.config.location=classpath:/application-test.yaml")
@ContextConfiguration(classes = Application.class)
class BaseTest extends Specification {

    @Autowired
    ProjectRepository projectRepository

    def setupSpec() {}

    def cleanupSpec() {}

    def setup() {
        projectRepository.deleteAll()
    }

    def cleanup() {
    }

    def restClient() {
        RestClient.builder()
            .baseUrl("http://localhost:8083")
            .requestFactory(new SimpleClientHttpRequestFactory())
            .build()
    }
}
