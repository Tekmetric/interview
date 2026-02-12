package com.interview.test.feature

import com.interview.test.BaseTest
import org.springframework.http.HttpStatus

class WelcomeAPITest extends BaseTest {

    def 'Welcome API should respond properly'() {
        given: 'The system was started having Welcome API set'
        def restClient = restClient()

        when: 'Welcome API is called'
        def response = restClient.get()
            .uri('/api/welcome')
            .retrieve()
            .toEntity(String.class)

        then: 'The response will be successful (status code 200)'
        response.statusCode == HttpStatus.OK
        response.body == 'Welcome to the interview project!'
    }
}