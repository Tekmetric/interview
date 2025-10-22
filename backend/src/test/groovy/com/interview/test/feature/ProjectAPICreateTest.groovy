package com.interview.test.feature

import com.interview.feature.project.ProjectDTO
import com.interview.feature.project.ProjectStatus
import com.interview.test.BaseTest
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import spock.lang.Stepwise

@Stepwise
class ProjectAPICreateTest extends BaseTest {

    def 'Project API - Create - simple insert should work properly'() {
        given: 'The system was started having Project API set'
        def restClient = restClient()
        def project = new ProjectDTO(null, 'Tekmetric', 'Car repair management service', null)

        when: 'Project API - Create - is called'
        def response = restClient.post()
            .uri('/api/projects')
            .body(project)
            .retrieve()
            .toEntity(new ParameterizedTypeReference<ProjectDTO>() {})

        then: 'The response will be successful, with a valid uid and status code 200'
        response.statusCode == HttpStatus.OK
        response.body.uid() != null
        response.body.name() == 'Tekmetric'
        response.body.description() == 'Car repair management service'
    }

    def 'Project API - Create - the system should validate input'() {
        given: 'The system was started having Project API set'
        def restClient = restClient()
        def project = new ProjectDTO(uid, name, description, projectStatus)

        when: 'Project API - Create is called'
        def response = restClient.post()
            .uri('/api/projects')
            .body(project)
            .retrieve()
            .toEntity(new ParameterizedTypeReference<ProjectDTO>() {})

        then: 'The system will validate input and will respond accordingly'
        def ex= thrown(HttpClientErrorException.BadRequest.class)
        ex.statusCode == HttpStatus.BAD_REQUEST

        where:
        uid     | name          | description                       | projectStatus         || expectedHttpStatus
        '1'     | 'Tekmetric'   | 'Car repair management service'   | null                  || HttpStatus.OK
        null    | 'Tekmetric'   | 'Car repair management service'   | ProjectStatus.ACTIVE  || HttpStatus.OK
    }

    def 'Project API - Create - user role should be denied from accessing this API'() {
        given: 'The system was started having Project API set'
        def restClient = restClient('user')
        def project = new ProjectDTO(null, 'Tekmetric', 'Car repair management service', null)

        when: 'Project API - Create - is called with a user role'
        def response = restClient.post()
            .uri('/api/projects')
            .body(project)
            .retrieve()
            .toEntity(new ParameterizedTypeReference<ProjectDTO>() {})

        then: 'The response will be successful, with a valid uid and status code 200'
        def ex= thrown(HttpClientErrorException.Forbidden.class)
        ex.statusCode == HttpStatus.FORBIDDEN
    }

    def 'Project API - Create - inserting same entity twice should result in 2 different saved entities'() {
        given: 'The system was started having Project API set'
        def restClient = restClient()
        def project = new ProjectDTO(null, 'Tekmetric', 'Car repair management service', null)

        when: 'Project API - Create - is called twice with the same request body'
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

        then: 'Execution of both calls will result in 2 different created entities'
        response1.body.uid() != response2.body.uid()

        response1.body.uid() != null
        response1.body.name() == 'Tekmetric'
        response1.body.description() == 'Car repair management service'

        response2.body.uid() != null
        response2.body.name() == 'Tekmetric'
        response2.body.description() == 'Car repair management service'
    }
}
