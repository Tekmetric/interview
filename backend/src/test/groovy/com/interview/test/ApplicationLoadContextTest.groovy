package com.interview.test

import com.interview.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import spock.lang.Specification;

@SpringBootTest
class ApplicationLoadContextTest extends Specification {

    @Autowired(required = false)
    private Application application

    def 'When context is loaded then all expected beans are created'() {
        expect: "The Application Context is successfully created"
        application
    }
}