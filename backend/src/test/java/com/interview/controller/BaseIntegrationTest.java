package com.interview.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.interview.config.SecurityConfig;
import com.interview.repository.CustomerRepository;

@WithMockUser
@Import(SecurityConfig.class)
abstract class BaseIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    @MockitoBean
    protected CustomerRepository customerRepository;

    protected abstract String protectedEndpoint();

    @Test
    @WithAnonymousUser
    void unauthenticatedRequest_returns401() throws Exception {
        mockMvc.perform(get(protectedEndpoint()))
                .andExpect(status().isUnauthorized());
    }
}
