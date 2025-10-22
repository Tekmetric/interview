package com.interview.test.feature

import com.interview.feature.project.ProjectDTO
import com.interview.feature.project.ProjectStatus
import com.interview.test.BaseTest
import com.interview.test.Page
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import spock.lang.Stepwise

@Stepwise
class ProjectAPITest extends BaseTest {

    def 'Project API - Retrieve all should work properly'() {
        given: 'The system was started having Project API set'
        def restClient = restClient()

        when: 'Project API is called'
        def response = restClient.get()
            .uri('/api/projects')
            .retrieve()
            .toEntity(new ParameterizedTypeReference<Page<ProjectDTO>>() {})

        then: 'The response will be successful (status code 200) but with no elements'
        response.statusCode == HttpStatus.OK
        response.body.pageMetadata.number() == 0
        response.body.content.isEmpty()
    }

    def 'Project API - Retrieve by id should work properly'() {
        given: 'The system was started having Project API set'
        def restClient = restClient()
        def project = new ProjectDTO('1', 'Tekmetric', 'Car repair service', ProjectStatus.PLANNED)
        restClient.put().uri('/api/projects').body(project).retrieve().toBodilessEntity()

        when: 'Project API - Retrieve by id is called'
        def response = restClient.get()
            .uri("/api/projects/${project.uid()}")
            .retrieve()
            .toEntity(ProjectDTO.class)

        then: 'The response will be successful (status code 200) and the saved entity should now be removed'
        response.statusCode == HttpStatus.OK
        response.body.uid() == '1'
        response.body.name() == 'Tekmetric'
        response.body.description() == 'Car repair service'
        response.body.status() == ProjectStatus.PLANNED

    }

    def 'Project API - Create should work properly'() {
        given: 'The system was started having Project API set'
        def restClient = restClient()
        def project = new ProjectDTO(null, 'Tekmetric', 'Car repair service', null)

        when: 'Project API is called'
        def response = restClient.post()
            .uri('/api/projects')
            .body(project)
            .retrieve()
            .toEntity(new ParameterizedTypeReference<ProjectDTO>() {})

        then: 'The response will be successful (status code 200)'
        response.statusCode == HttpStatus.OK
        response.body.uid() != null
        response.body.name() == 'Tekmetric'
        response.body.description() == 'Car repair service'
    }

    def 'Project API - Create should work properly when the same request is executed twice'() {
        given: 'The system was started having Project API set'
        def restClient = restClient()
        def project = new ProjectDTO(null, 'Tekmetric', 'Car repair service', null)

        when: 'Project API is called twice with the same request body'
        def response1 = restClient.post()
            .uri('/api/projects')
            .body(project)
            .retrieve()
            .toEntity(new ParameterizedTypeReference<ProjectDTO>() {})
        def response2 = restClient.post()
            .uri('/api/projects')
            .body(project)
            .retrieve()
            .toEntity(new ParameterizedTypeReference<ProjectDTO>() {})

        then: 'Both calls will result in 2 different created entities'
        response1.body.uid() != response2.body.uid()

        response1.body.uid() != null
        response1.body.name() == 'Tekmetric'
        response1.body.description() == 'Car repair service'

        response2.body.uid() != null
        response2.body.name() == 'Tekmetric'
        response2.body.description() == 'Car repair service'
    }

    def 'Project API - Create API should work properly when requests count is below rate limit'() {
        given: 'The system was started having Project API set'
        def restClient = restClient()
        def project = new ProjectDTO(null, 'Tekmetric', 'Car repair service', null)

        when: "Project API is called $simultaneousRequests times"
        def responses = (1..simultaneousRequests).collect {
            restClient.post()
                .uri('/api/projects')
                .body(project)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<ProjectDTO>() {})
        }

        then: 'The response will be successful (status code 200)'
        responses.every { it.statusCode == expectedStatus }

        where:
        simultaneousRequests || expectedStatus
        1                    || HttpStatus.OK
        2                    || HttpStatus.OK
    }

    def 'Project API - Create API should throw exception when requests count is above rate limit'() {
        given: 'The system was started having Project API set'
        def restClient = restClient()
        def project = new ProjectDTO(null, 'Tekmetric', 'Car repair service', null)

        when: "Project API is called 100 times"
        def responses = (1..100).collect {
            def result = restClient.post()
                .uri('/api/projects')
                .body(project)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<ProjectDTO>() {})
        }

        then: 'The server will send a Too Many Requests http status code'
        def ex= thrown(HttpClientErrorException.TooManyRequests)
        ex.statusCode == HttpStatus.TOO_MANY_REQUESTS
    }

    def 'Project API - Update should work properly'() {
        given: 'The system was started having Project API set'
        def restClient = restClient()
        def project = new ProjectDTO('1', 'Tekmetric', 'Car repair service', ProjectStatus.PLANNED)

        when: 'Project API is called'
        def response = restClient.put()
            .uri('/api/projects')
            .body(project)
            .retrieve()
            .toEntity(new ParameterizedTypeReference<ProjectDTO>() {})

        then: 'The response will be successful (status code 200)'
        response.statusCode == HttpStatus.OK
        response.body.uid() == '1'
        response.body.name() == 'Tekmetric'
        response.body.description() == 'Car repair service'
    }

    def 'Project API - Update should work properly when the same request is executed twice'() {
        given: 'The system was started having Project API set'
        def restClient = restClient()
        def project = new ProjectDTO('1', 'Tekmetric', 'Car repair service', ProjectStatus.PLANNED)

        when: 'Project API is called twice with the same request body'
        def response1 = restClient.put()
            .uri('/api/projects')
            .body(project)
            .retrieve()
            .toEntity(new ParameterizedTypeReference<ProjectDTO>() {})
        def response2 = restClient.put()
            .uri('/api/projects')
            .body(project)
            .retrieve()
            .toEntity(new ParameterizedTypeReference<ProjectDTO>() {})

        then: 'Both calls will result in having a single saved entity -> idempotency'
        response1.body.uid() == response2.body.uid()
        response1.body.name() == response2.body.name()
        response1.body.description() == response2.body.description()

        response1.body.name() == 'Tekmetric'
        response1.body.description() == 'Car repair service'
    }

    def 'Project API - Delete should work properly'() {
        given: 'The system was started having Project API set'
        def restClient = restClient()
        def project = new ProjectDTO('1', 'Tekmetric', 'Car repair service', ProjectStatus.PLANNED)
        restClient.put().uri('/api/projects').body(project).retrieve().toBodilessEntity()

        when: 'Project API - Delete is called'
        def response = restClient.delete()
            .uri("/api/projects/${project.uid()}")
            .retrieve()
            .toBodilessEntity()
        def allProjects = restClient.get()
            .uri('/api/projects')
            .retrieve()
            .toEntity(new ParameterizedTypeReference<Page<ProjectDTO>>() {})

        then: 'The response will be successful (status code 200) and the saved entity should now be removed'
        response.statusCode == HttpStatus.NO_CONTENT
        allProjects.body.content.isEmpty()
    }
}