package com.interview.test.feature

import com.interview.feature.project.ProjectDTO
import com.interview.feature.project.ProjectStatus
import com.interview.test.BaseTest
import com.interview.test.Page
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException

class ProjectAPIRetrieveTest extends BaseTest {

    def 'Project API - Retrieve all - simple fetch all should work properly'() {
        given: 'The system was started having Project API set'
        def restClient = restClient()

        when: 'Project API - Retrieve all - is called'
        def response = restClient.get()
            .uri('/api/projects')
            .retrieve()
            .toEntity(new ParameterizedTypeReference<Page<ProjectDTO>>() {})

        then: 'The response will be successful (status code 200) with 4 elements'
        response.statusCode == HttpStatus.OK
        response.body.content.size() > 0
    }

    def 'Project API - Retrieve all - should work properly when requests count is below rate limit'() {
        given: 'The system was started having Project API set'
        def restClient = restClient()

        when: "Project API - Retrieve all - is called $simultaneousRequests times"
        def responses = (1..simultaneousRequests).collect {
            restClient.get()
                .uri('/api/projects')
                .retrieve()
                .toEntity(ProjectDTO.class)
        }

        then: 'The response will be successful (status code 200)'
        responses.every { it.statusCode == expectedStatus }

        where:
        simultaneousRequests || expectedStatus
        1                    || HttpStatus.OK
        2                    || HttpStatus.OK
    }

    def 'Project API - Retrieve all - should throw exception when requests count is above rate limit'() {
        given: 'The system was started having Project API set'
        def restClient = restClient()

        when: "Project API - Create - is called 100 times"
        def responses = (1..100).collect {
            restClient.get()
                .uri('/api/projects')
                .retrieve()
                .toEntity(ProjectDTO.class)
        }

        then: 'The server will send a Too Many Requests http status code'
        def ex= thrown(HttpClientErrorException.TooManyRequests)
        ex.statusCode == HttpStatus.TOO_MANY_REQUESTS
    }

    def 'Project API - Retrieve by id - should work properly'() {
        given: 'The system was started having Project API set'
        def restClient = restClient()
        def project = restClient.post()
            .uri('/api/projects')
            .body(new ProjectDTO(null, 'Tekmetric', 'Car repair management service', null))
            .retrieve()
            .toEntity(ProjectDTO.class)
            .body

        when: 'Project API - Retrieve by id - is called'
        def response = restClient.get()
            .uri("/api/projects/${project.uid()}")
            .retrieve()
            .toEntity(ProjectDTO.class)

        then: 'The response will be successful (status code 200) and the saved entity should now be removed'
        response.statusCode == HttpStatus.OK
        response.body.uid() == project.uid()
        response.body.name() == 'Tekmetric'
        response.body.description() == 'Car repair management service'
        response.body.status() == ProjectStatus.PLANNED
    }
}