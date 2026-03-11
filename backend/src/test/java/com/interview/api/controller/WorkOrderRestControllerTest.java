package com.interview.api.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.interview.api.mapper.LaborLineItemApiMapper;
import com.interview.api.mapper.PartLineItemApiMapper;
import com.interview.api.mapper.WorkOrderApiMapper;
import com.interview.service.WorkOrderService;
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

@WebMvcTest(WorkOrderRestController.class)
@AutoConfigureMockMvc(addFilters = false)
class WorkOrderRestControllerTest {
    // e2e coverage in WorkOrderRestControllerIT

    private static final UUID ID = UUID.randomUUID();

    @MockitoBean
    private WorkOrderService workOrderService;

    @MockitoBean
    private WorkOrderApiMapper workOrderApiMapper;

    @MockitoBean
    private PartLineItemApiMapper partLineItemApiMapper;

    @MockitoBean
    private LaborLineItemApiMapper laborLineItemApiMapper;

    @Autowired
    private MockMvc mockMvc;

    static Stream<MockHttpServletRequestBuilder> mutationEndpoints() {
        return Stream.of(
                post("/work-orders"),
                put("/work-orders/{id}", ID),
                post("/work-orders/{workOrderId}/part-line-items", ID),
                post("/work-orders/{workOrderId}/labor-line-items", ID));
    }

    @ParameterizedTest
    @MethodSource("mutationEndpoints")
    // intentionally not exhaustive of all invalid input, serves to guarantee that bean validation is wired correctly
    void beanValidationIsHandled(MockHttpServletRequestBuilder request) throws Exception {
        mockMvc.perform(request.contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @MethodSource("mutationEndpoints")
    void malformedJsonReturnsBadRequest(MockHttpServletRequestBuilder request) throws Exception {
        mockMvc.perform(request.contentType(MediaType.APPLICATION_JSON).content("not json"))
                .andExpect(status().isBadRequest());
    }
}
