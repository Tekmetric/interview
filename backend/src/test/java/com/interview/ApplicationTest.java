package com.interview;

import com.interview.framework.config.TestConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ContextConfiguration(classes = TestConfig.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ExtendWith(SpringExtension.class)
public class ApplicationTest {

    /** Positive test for loading the spring context. */
    @Test
    void contextLoadsTest(){
    }
}
