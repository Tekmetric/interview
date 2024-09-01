package com.interview.resource;

import com.interview.controller.CarController;
import com.interview.entity.Car;
import com.interview.service.CarService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CarController.class)
public class CarControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    CarService carService;

    @Test
    public void shouldReturnACarWhenDataIsAlreadyPersisted() throws Exception {
        var testCar = new Car(null, "TestCar");
        when(carService.getCarById(anyLong())).thenReturn(testCar);

        mockMvc.perform(get("/api/cars/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("TestCar"));
    }
}