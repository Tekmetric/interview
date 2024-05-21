package com.interview.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.controller.ServiceAppointmentController;
import com.interview.dto.ServiceAppointmentDTO;
import com.interview.service.ServiceAppointmentService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.time.temporal.ChronoUnit;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ServiceAppointmentController.class)
class ServiceAppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ServiceAppointmentService serviceAppointmentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testCreateServiceAppointment() throws Exception {
        ServiceAppointmentDTO appointment = new ServiceAppointmentDTO();
        appointment.setId(1L);
        appointment.setDescription("Oil change");
        appointment.setAppointmentDate(new Date());
        appointment.setCustomerId(1L);

        ServiceAppointmentDTO appointmentDTO = new ServiceAppointmentDTO();
        appointmentDTO.setDescription("Oil change");
        appointmentDTO.setAppointmentDate( Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        appointmentDTO.setCustomerId(1L);

        Mockito.when(serviceAppointmentService.createServiceAppointment(
                        Mockito.anyObject()))
                .thenReturn(appointment);

        mockMvc.perform(post("/api/serviceappointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appointmentDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is(appointment.getDescription())))
                .andExpect(jsonPath("$.customerId", is(appointment.getCustomerId().intValue())));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testDeleteServiceAppointment() throws Exception {
        Long appointmentId = 1L;

        Mockito.doNothing().when(serviceAppointmentService).deleteServiceAppointment(appointmentId);

        mockMvc.perform(delete("/api/serviceappointments/{id}", appointmentId))
                .andExpect(status().isNoContent());
    }
}
