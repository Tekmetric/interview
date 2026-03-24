package com.interview.controller;

import com.interview.Application;
import com.interview.dto.response.MechanicResponse;
import com.interview.facade.MechanicFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
public class MechanicControllerTest {

    private static final Long MECHANIC_ID = 1L;

    @MockitoBean
    private MechanicFacade mechanicFacade;
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).dispatchOptions(true).build();
    }

    @Test
    public void GIVEN_mechanicId_WHEN_getMechanicId_THEN_200() throws Exception {
        when(mechanicFacade.getMechanic(eq(MECHANIC_ID))).thenReturn(MechanicResponse.builder().build());

        mockMvc.perform(get("/api/v1/mechanic/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(mechanicFacade).getMechanic(eq(MECHANIC_ID));
        verifyNoMoreInteractions(mechanicFacade);
    }
}
