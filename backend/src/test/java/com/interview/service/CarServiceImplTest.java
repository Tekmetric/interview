package com.interview.service;

import com.interview.entity.Car;
import com.interview.exception.CarNotFoundException;
import com.interview.repository.CarRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarServiceImplTest {
    @InjectMocks
    CarServiceImpl carService;

    @Mock
    private CarRepository carRepository;

    @Test
    @DisplayName("when getCarById is called with an valid car id, expect exception to be returned")
    void getCarByIdGivenInvalidId() {
        assertThrows(CarNotFoundException.class, () -> carService.getCarById(-1), "Expected exception when invalid / not found car id is provided");
    }

    @Test
    @DisplayName("when getCarById is called with valid car id, expect returned car id and name to match")
    void getCarByIdGivenValidId() {
        // given
        var wantedCar = new Car(123L, "Test");
        when(carRepository.findById(anyLong())).thenReturn(Optional.of(wantedCar));

        // when
        var got = carService.getCarById(123);

        // then
        assertEquals(wantedCar.getId(), got.getId(), "expect same car id");
        assertEquals(wantedCar.getName(), got.getName(), "expect same name of the car given car id");
    }
}