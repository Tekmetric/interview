package com.interview.test.feature

import com.interview.feature.project.ProjectDTO
import com.interview.feature.project.ProjectStatus
import com.interview.test.BaseTest
import com.interview.test.Page
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpStatus
import spock.lang.Stepwise

@Stepwise
class ProjectAPIRetrieveTest extends BaseTest {

    def 'Project API - Retrieve all - simple fetch all should work properly'() {
        given: 'The system was started having Project API set'
        def restClient = restClient()
        restClient.put().uri('/api/projects').body(new ProjectDTO('1', 'Tekmetric', 'Car repair service', ProjectStatus.PLANNED)).retrieve().toBodilessEntity()
        restClient.put().uri('/api/projects').body(new ProjectDTO('2', 'GreenHub', 'Sustainability project tracking application', ProjectStatus.PLANNED)).retrieve().toBodilessEntity()
        restClient.put().uri('/api/projects').body(new ProjectDTO('3', 'HealthSync', 'Medical data synchronization microservice', ProjectStatus.PLANNED)).retrieve().toBodilessEntity()
        restClient.put().uri('/api/projects').body(new ProjectDTO('4', 'FinTrack', 'Personal finance tracking API', ProjectStatus.PLANNED)).retrieve().toBodilessEntity()


        when: 'Project API - Retrieve all - is called'
        def response = restClient.get()
            .uri('/api/projects')
            .retrieve()
            .toEntity(new ParameterizedTypeReference<Page<ProjectDTO>>() {})

        then: 'The response will be successful (status code 200) with 4 elements'
        response.statusCode == HttpStatus.OK
        response.body.pageMetadata.totalElements() == 4
        response.body.pageMetadata.totalPages() == 1
        response.body.content.size() == 4
    }

    def 'Project API - Retrieve by id - should work properly'() {
        given: 'The system was started having Project API set'
        def restClient = restClient()
        def project = new ProjectDTO('1', 'Tekmetric', 'Car repair service', ProjectStatus.PLANNED)
        restClient.put().uri('/api/projects').body(project).retrieve().toBodilessEntity()

        when: 'Project API - Retrieve by id - is called'
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
}