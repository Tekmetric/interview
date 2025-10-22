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
class ProjectAPIDeleteTest extends BaseTest {

    def 'Project API - Delete - simple delete should work properly'() {
        given: 'The system was started having Project API set'
        def restClient = restClient()
        def project = new ProjectDTO('1', 'Tekmetric', 'Car repair management service', ProjectStatus.PLANNED)
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
