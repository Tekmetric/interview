package com.interview.resource;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.dto.car.CarCreateRequestDTO;
import com.interview.dto.car.CarDTO;
import com.interview.dto.car.CarUpdateRequestDTO;
import com.interview.dto.page.PageResponseDTO;
import com.interview.service.CarService;
import java.util.List;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@Import(TestConfig.class)
@WebMvcTest(CarResource.class)
class CarResourceTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private CarService carService;

  @Autowired private ObjectMapper objectMapper;

  private static final EasyRandomParameters randomParams = new EasyRandomParameters();

  private static final EasyRandom easyRandom = new EasyRandom(randomParams);

  @Test
  void createCar_returnsCreatedCar() throws Exception {
    final CarCreateRequestDTO request = easyRandom.nextObject(CarCreateRequestDTO.class);
    final CarDTO response = easyRandom.nextObject(CarDTO.class);

    Mockito.when(carService.createCar(any())).thenReturn(response);

    mockMvc
        .perform(
            post("/cars")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(response.getId()));
  }

  @Test
  void getCarById_returnsCar() throws Exception {
    final CarDTO response = easyRandom.nextObject(CarDTO.class);

    Mockito.when(carService.getCarById(eq(1L))).thenReturn(response);

    mockMvc
        .perform(get("/cars/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(response.getId()));
  }

  @Test
  void deleteCarById_returnsDeletedCar() throws Exception {
    final CarDTO response = easyRandom.nextObject(CarDTO.class);

    Mockito.when(carService.deleteCarById(eq(1L))).thenReturn(response);

    mockMvc
        .perform(delete("/cars/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(response.getId()));
  }

  @Test
  void updateCar_returnsUpdatedCar() throws Exception {
    final CarUpdateRequestDTO request = easyRandom.nextObject(CarUpdateRequestDTO.class);
    final CarDTO response = easyRandom.nextObject(CarDTO.class);

    Mockito.when(carService.updateCar(eq(1L), any())).thenReturn(response);

    mockMvc
        .perform(
            put("/cars/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(response.getId()));
  }

  @Test
  void getCars_returnsPagedCars() throws Exception {
    final CarDTO car1 = easyRandom.nextObject(CarDTO.class);
    final CarDTO car2 = easyRandom.nextObject(CarDTO.class);
    final PageResponseDTO<CarDTO> page =
        PageResponseDTO.<CarDTO>builder()
            .content(List.of(car1, car2))
            .page(0)
            .size(2)
            .totalElements(2L)
            .totalPages(1)
            .last(false)
            .build();

    Mockito.when(carService.getCars(any(String.class), any(PageRequest.class))).thenReturn(page);

    mockMvc
        .perform(get("/cars").param("page", "0").param("size", "2").param("query", ""))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(2));
  }
}
