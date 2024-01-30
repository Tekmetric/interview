package com.interview;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.exception.VehicleNotFoundException;
import com.interview.resource.dto.VehicleCreationRequest;
import com.interview.resource.dto.VehicleDto;
import com.interview.resource.dto.VehicleStateDto;
import com.interview.resource.dto.VehicleUpdateRequest;
import com.interview.security.resource.dto.AuthenticationResponse;
import com.interview.security.resource.dto.LoginRequest;
import com.interview.security.resource.dto.RegistrationRequest;

@SpringBootTest
@AutoConfigureMockMvc
public class VehicleIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String PATH = "/api/v1/vehicles";

    private static final String AUTH_PATH = "/api/v1/auth";

    @Test
    public void e2eFlow() throws Exception {
        // security check 
        MockHttpServletResponse unauthorizedResponse = mockMvc.perform(
            get(PATH + "/{id}", 1L).accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        assertEquals(HttpStatus.FORBIDDEN.value(), unauthorizedResponse.getStatus());

        // register
        RegistrationRequest registrationRequest = RegistrationRequest.builder()
            .email("ibrahimb@tekmetric.com")
            .password("ib123321bi")
            .firstName("ibrahim")
            .lastName("bilge")
            .build();

        MockHttpServletResponse registrationResponse = mockMvc.perform(
            post(AUTH_PATH + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationRequest))
        ).andReturn().getResponse();

        assertEquals(HttpStatus.OK.value(), registrationResponse.getStatus());

        // login
        LoginRequest loginRequest = LoginRequest.builder()
            .email("ibrahimb@tekmetric.com")
            .password("ib123321bi")
            .build();

        MockHttpServletResponse loginResponse = mockMvc.perform(
            post(AUTH_PATH + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
        ).andReturn().getResponse();

        AuthenticationResponse authenticationResponse = 
            objectMapper.readValue(loginResponse.getContentAsString(), AuthenticationResponse.class);

        assertNotNull(authenticationResponse.getToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authenticationResponse.getToken());

        // create vehicle
        VehicleCreationRequest vehicleCreationRequest = VehicleCreationRequest.builder()
            .licensePlate("test-lp")
            .brand("test-brand")
            .model("test-model")
            .build();

        MockHttpServletResponse vehicleCreationResponse = mockMvc.perform(
            post(PATH)
                .content(objectMapper.writeValueAsString(vehicleCreationRequest))
                .headers(headers)
        ).andReturn().getResponse();

        assertEquals(HttpStatus.CREATED.value(), vehicleCreationResponse.getStatus());
        assertTrue(vehicleCreationResponse.containsHeader("Location"));
        String vehicleUrl = vehicleCreationResponse.getHeader("Location");

        // get vehicle
        MockHttpServletResponse getVehicleResponse = mockMvc.perform(
            get(vehicleUrl).headers(headers)
        ).andReturn().getResponse();

        assertEquals(HttpStatus.OK.value(), getVehicleResponse.getStatus());
        
        VehicleDto vehicleDto = 
            objectMapper.readValue(getVehicleResponse.getContentAsString(), VehicleDto.class);
        
        assertNotNull(vehicleDto);
        assertNotNull(vehicleDto.getId());
        assertEquals("test-lp", vehicleDto.getLicensePlate());
        assertNotNull(vehicleDto.getCreationDate());
        assertEquals(VehicleStateDto.NOT_STARTED, vehicleDto.getState());

        // update vehicle
        VehicleUpdateRequest vehicleUpdateRequest = VehicleUpdateRequest.builder()
            .licensePlate(vehicleDto.getLicensePlate())
            .brand(vehicleDto.getBrand())
            .model("new-test-model")
            .registrationYear(2018)
            .cost(100.0)
            .build();

        MockHttpServletResponse vehicleUpdateResponse = mockMvc.perform(
            put(vehicleUrl)
                .content(objectMapper.writeValueAsString(vehicleUpdateRequest))
                .headers(headers)
        ).andReturn().getResponse();

        assertEquals(HttpStatus.OK.value(), vehicleUpdateResponse.getStatus());

        VehicleDto updatedVehicleDto = 
            objectMapper.readValue(vehicleUpdateResponse.getContentAsString(), VehicleDto.class);

        assertNotNull(updatedVehicleDto);
        assertNotNull(updatedVehicleDto.getId());
        assertEquals(vehicleDto.getId(), updatedVehicleDto.getId());
        assertEquals(vehicleDto.getLicensePlate(), updatedVehicleDto.getLicensePlate());
        assertNotEquals(vehicleDto.getModel(), updatedVehicleDto.getModel());
        assertNotNull(updatedVehicleDto.getRegistrationYear());
        assertNotEquals(updatedVehicleDto.getCreationDate(), updatedVehicleDto.getLastModificationDate());
    
        // delete vehicle
        MockHttpServletResponse deleteVehicleResponse = mockMvc.perform(
            delete(vehicleUrl).headers(headers)
        ).andReturn().getResponse();

        assertEquals(HttpStatus.OK.value(), deleteVehicleResponse.getStatus());

        Long deletedVehicleId = 
            objectMapper.readValue(deleteVehicleResponse.getContentAsString(), Long.class);

        assertEquals(vehicleDto.getId(), deletedVehicleId);

        // get vehicle
        mockMvc.perform(
            get(vehicleUrl).headers(headers)
        )
        .andExpect(status().isNotFound())
        .andExpect(result -> assertTrue(result.getResolvedException() instanceof VehicleNotFoundException));
    }
}
