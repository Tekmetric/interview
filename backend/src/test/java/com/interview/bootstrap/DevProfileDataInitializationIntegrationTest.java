package com.interview.bootstrap;

import static org.assertj.core.api.Assertions.assertThat;

import com.interview.auth.dao.UserRepository;
import com.interview.customer.dao.CustomerRepository;
import com.interview.workorder.dao.WorkOrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("dev")
class DevProfileDataInitializationIntegrationTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private WorkOrderRepository workOrderRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldLoadTestDataScriptWhenDevProfileIsActive() {
        assertThat(customerRepository.count()).isEqualTo(2);
        assertThat(workOrderRepository.count()).isEqualTo(4);
        assertThat(userRepository.count()).isEqualTo(2);
    }
}
