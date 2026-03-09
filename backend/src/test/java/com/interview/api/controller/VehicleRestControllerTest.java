package com.interview.api.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.interview.api.mapper.VehicleApiMapper;
import com.interview.service.VehicleService;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@WebMvcTest(VehicleRestController.class)
@AutoConfigureMockMvc(addFilters = false)
class VehicleRestControllerTest {
    // e2e coverage in VehicleRestControllerIT

    private static final String INVALID_BODY = """
            {"vin":"INVALID"}""";

    @MockitoBean
    private VehicleService vehicleService;

    @MockitoBean
    private VehicleApiMapper vehicleApiMapper;

    @Autowired
    private MockMvc mockMvc;

    static Stream<MockHttpServletRequestBuilder> mutationEndpoints() {
        return Stream.of(post("/vehicles"), put("/vehicles/{id}", UUID.randomUUID()));
    }

    @ParameterizedTest
    @MethodSource("mutationEndpoints")
    void beanValidationIsHandled(MockHttpServletRequestBuilder request) throws Exception {
        // intentionally not exhaustive of all invalid input, serves to guarantee that bean validation is wired
        // correctly
        mockMvc.perform(request.contentType(MediaType.APPLICATION_JSON).content(INVALID_BODY))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @MethodSource("mutationEndpoints")
    void malformedJsonReturnsBadRequest(MockHttpServletRequestBuilder request) throws Exception {
        mockMvc.perform(request.contentType(MediaType.APPLICATION_JSON).content("not json"))
                .andExpect(status().isBadRequest());
    }
}
