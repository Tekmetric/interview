package com.interview.test.feature

import com.interview.feature.project.ProjectDTO
import com.interview.feature.project.ProjectStatus
import com.interview.test.BaseTest
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import spock.lang.Stepwise

@Stepwise
class ProjectAPITest_Save extends BaseTest {

    def 'Project API - Save - simple save should work properly'() {
        given: 'The system was started having Project API set'
        def restClient = restClient()
        def project = new ProjectDTO('1', 'Tekmetric', 'Car repair management service', ProjectStatus.PLANNED)

        when: 'Project API - Save - is called'
        def response = restClient.put()
            .uri('/api/projects')
            .body(project)
            .retrieve()
            .toEntity(new ParameterizedTypeReference<ProjectDTO>() {})

        then: 'The response will be successful, with a valid uid and status code 200'
        response.statusCode == HttpStatus.OK
        response.body.uid() == '1'
        response.body.name() == 'Tekmetric'
        response.body.description() == 'Car repair management service'
    }

    def 'Project API - Save - user role should be able to access this API'() {
        given: 'The system was started having Project API set'
        def restClient = restClient('user')
        def project = new ProjectDTO('1', 'Tekmetric', 'Car repair management service', ProjectStatus.PLANNED)

        when: 'Project API - Save - is called'
        def response = restClient.put()
            .uri('/api/projects')
            .body(project)
            .retrieve()
            .toEntity(new ParameterizedTypeReference<ProjectDTO>() {})

        then: 'The response will be successful, with a valid uid and status code 200'
        response.statusCode == HttpStatus.OK
        response.body.uid() == '1'
        response.body.name() == 'Tekmetric'
        response.body.description() == 'Car repair management service'
    }

    def 'Project API - Save - the system should validate input'() {
        given: 'The system was started having Project API set'
        def restClient = restClient()
        def project = new ProjectDTO(uid, name, description, projectStatus)

        when: 'Project API - Save is called'
        def response = restClient.put()
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

    def 'Project API - Save - should work properly when the same request is executed twice'() {
        given: 'The system was started having Project API set'
        def restClient = restClient()
        def project = new ProjectDTO('1', 'Tekmetric', 'Car repair management service', ProjectStatus.PLANNED)

        when: 'Project API - Save - is called twice with the same request body'
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
        response1.body.description() == 'Car repair management service'
    }
}
