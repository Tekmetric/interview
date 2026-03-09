package com.interview.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.interview.api.mapper.VehicleApiMapperImpl;
import com.interview.config.SecurityConfig;
import com.interview.domain.Vehicle;
import com.interview.domain.Vin;
import com.interview.service.VehicleService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(VehicleRestController.class)
@Import({SecurityConfig.class, VehicleApiMapperImpl.class})
class VehicleRestControllerIT {

    private static final UUID TEST_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final Vin TEST_VIN = new Vin("1HGBH41JXMN109186");
    private static final Vehicle TEST_VEHICLE = new Vehicle(TEST_ID, TEST_VIN);

    @MockitoBean
    private VehicleService vehicleService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAllVehiclesReturnsOk() throws Exception {
        when(vehicleService.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(TEST_VEHICLE)));

        mockMvc.perform(get("/vehicles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(TEST_ID.toString()))
                .andExpect(jsonPath("$.content[0].vin").value(TEST_VIN.vinString()));
    }

    @Test
    void getVehicleByIdReturnsOk() throws Exception {
        when(vehicleService.findById(TEST_ID)).thenReturn(TEST_VEHICLE);

        mockMvc.perform(get("/vehicles/{id}", TEST_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TEST_ID.toString()))
                .andExpect(jsonPath("$.vin").value(TEST_VIN.vinString()));
    }

    @Test
    void createVehicleReturnsCreated() throws Exception {
        when(vehicleService.create(any(Vehicle.class))).thenReturn(TEST_VEHICLE);

        mockMvc.perform(post("/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"vin":"1HGBH41JXMN109186"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/vehicles/" + TEST_ID))
                .andExpect(jsonPath("$.id").value(TEST_ID.toString()))
                .andExpect(jsonPath("$.vin").value(TEST_VIN.vinString()));
    }

    @Test
    void createVehicleWithInvalidBeanReturnsBadRequest() throws Exception {
        //intentionally not exhaustive, serves to guarantee that bean validation is wired correctly
        mockMvc.perform(post("/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"vin":"INVALID"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateVehicleReturnsOk() throws Exception {
        when(vehicleService.update(any(UUID.class), any(Vehicle.class))).thenReturn(TEST_VEHICLE);

        mockMvc.perform(put("/vehicles/{id}", TEST_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"vin":"1HGBH41JXMN109186"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TEST_ID.toString()))
                .andExpect(jsonPath("$.vin").value(TEST_VIN.vinString()));
    }

    @Test
    void deleteVehicleReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/vehicles/{id}", TEST_ID))
                .andExpect(status().isNoContent());
    }

}
