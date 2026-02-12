package com.interview.test.feature

import com.interview.feature.project.ProjectDTO
import com.interview.feature.project.ProjectStatus
import com.interview.test.BaseTest
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import spock.lang.Stepwise

@Stepwise
class ProjectAPIUpdateTest extends BaseTest {

    def 'Project API - Update - simple update should work properly'() {
        given: 'A project is already created into the system'
        def restClient = restClient()
        def initialProject = restClient.post()
            .uri('/api/projects')
            .body(new ProjectDTO(null, 'Tekmetric', 'Car repair management service', null))
            .retrieve()
            .toEntity(ProjectDTO.class)
            .body

        when: 'The existing project is updated with some different values'
        def updatedProject = restClient.put()
            .uri("/api/projects/${initialProject.uid()}")
            .body(new ProjectDTO(initialProject.uid(), 'A new name!', 'A different business type!', ProjectStatus.PLANNED))
            .retrieve()
            .toEntity(ProjectDTO.class)

        then: 'The response will be successful and the initial project will be updated with new values'
        updatedProject.statusCode == HttpStatus.OK
        updatedProject.body.uid() == initialProject.uid()
        updatedProject.body.name() == 'A new name!'
        updatedProject.body.description() == 'A different business type!'
        updatedProject.body.status() == ProjectStatus.PLANNED
    }

    def 'Project API - Update - user role should be able to access this API'() {
        given: 'A project is already created into the system'
        def initialProject = restClient('admin').post()
            .uri('/api/projects')
            .body(new ProjectDTO(null, 'Tekmetric', 'Car repair management service', null))
            .retrieve()
            .toEntity(ProjectDTO.class)
            .body

        when: 'The existing project is updated by a user role'
        def updatedProject = restClient('user').put()
            .uri("/api/projects/${initialProject.uid()}")
            .body(new ProjectDTO(initialProject.uid(), 'A new name!', 'A different business type!', ProjectStatus.PLANNED))
            .retrieve()
            .toEntity(ProjectDTO.class)

        then: 'The response will be successful and the initial project will be updated with new values'
        updatedProject.statusCode == HttpStatus.OK
        updatedProject.body.uid() == initialProject.uid()
        updatedProject.body.name() == 'A new name!'
        updatedProject.body.description() == 'A different business type!'
        updatedProject.body.status() == ProjectStatus.PLANNED
    }

    def 'Project API - Update - the system should validate input'() {
        given: 'A project is already created into the system'
        def restClient = restClient()
        def initialProject = restClient.post()
            .uri('/api/projects')
            .body(new ProjectDTO(null, 'Tekmetric', 'Car repair management service', null))
            .retrieve()
            .toEntity(ProjectDTO.class)
            .body

        when: 'The existing project is updated with new values'
        restClient.put()
            .uri("/api/projects/${initialProject.uid()}")
            .body(new ProjectDTO(uid, name, description, projectStatus))
            .retrieve()
            .toEntity(ProjectDTO.class)

        then: 'The system will validate input and will respond accordingly'
        def ex= thrown(HttpClientErrorException.BadRequest.class)
        ex.statusCode == HttpStatus.BAD_REQUEST

        where:
        uid     | name          | description                       | projectStatus         || expectedHttpStatus
        '1'     | 'Tekmetric'   | 'Car repair management service'   | null                  || HttpStatus.OK
        null    | 'Tekmetric'   | 'Car repair management service'   | ProjectStatus.ACTIVE  || HttpStatus.OK
    }

    def 'Project API - Update - should work properly when the same request is executed twice'() {
        given: 'A project is already created into the system'
        def restClient = restClient()
        def initialProject = restClient.post()
            .uri('/api/projects')
            .body(new ProjectDTO(null, 'Tekmetric', 'Car repair management service', null))
            .retrieve()
            .toEntity(ProjectDTO.class)
            .body

        when: 'The existing project is updated twice with the same new values'
        def newProject = new ProjectDTO(initialProject.uid(), 'A new name!', 'A different business type!', ProjectStatus.PLANNED)
        def response1 = restClient.put()
            .uri("/api/projects/${initialProject.uid()}")
            .body(newProject)
            .retrieve()
            .toEntity(ProjectDTO.class)
            .body
        def response2 = restClient.put()
            .uri("/api/projects/${initialProject.uid()}")
            .body(newProject)
            .retrieve()
            .toEntity(ProjectDTO.class)
            .body

        then: 'Both calls will result in having a single saved entity -> idempotency'
        response1.uid() == response2.uid()
        response1.name() == response2.name()
        response1.description() == response2.description()

        response1.name() == 'A new name!'
        response1.description() == 'A different business type!'
    }
}
