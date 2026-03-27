package com.interview.test.feature

import com.interview.feature.project.ProjectDTO
import com.interview.test.BaseTest
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException

class ProjectAPIDeleteTest extends BaseTest {

    def 'Project API - Delete - simple delete should work properly'() {
        given: 'The system was started having one project already saved into the system'
        def restClient = restClient()
        def existingProject = restClient.get()
            .uri("/api/projects/d1e2f3a4-b5c6-47d8-9e0f-445566778899")
            .retrieve()
            .toEntity(ProjectDTO.class)
        assert existingProject.body.uid() != null

        when: 'The project is deleted'
        def response = restClient.delete()
            .uri("/api/projects/d1e2f3a4-b5c6-47d8-9e0f-445566778899")
            .retrieve()
            .toBodilessEntity()
        assert response.statusCode == HttpStatus.NO_CONTENT

        restClient.get()
            .uri("/api/projects/d1e2f3a4-b5c6-47d8-9e0f-445566778899")
            .retrieve()
            .toEntity(ProjectDTO.class)

        then: 'The project will be removed'
        def ex= thrown(HttpClientErrorException.NotFound.class)
        ex.statusCode == HttpStatus.NOT_FOUND
    }

    def 'Project API - Delete - the system should validate input'() {
        given: 'The system was started having Project API set'
        def restClient = restClient()

        when: 'Project API - Delete is called with a random project uid'
        def response = restClient.delete()
            .uri("/api/projects/123456")
            .retrieve()
            .toEntity(new ParameterizedTypeReference<ProjectDTO>() {})

        then: 'The system will validate input and will respond with a not found response'
        def ex= thrown(HttpClientErrorException.NotFound.class)
        ex.statusCode == HttpStatus.NOT_FOUND
    }
}
