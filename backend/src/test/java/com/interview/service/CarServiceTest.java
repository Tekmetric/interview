package com.interview.service;

import com.interview.dto.CarRequest;
import com.interview.dto.CarResponse;
import com.interview.exception.CarNotFoundException;
import com.interview.exception.DuplicateVinException;
import com.interview.exception.InvalidCarDataException;
import com.interview.mapper.CarMapper;
import com.interview.model.Car;
import com.interview.model.CarStatus;
import com.interview.model.FuelType;
import com.interview.repository.CarRepository;
import com.interview.validator.CarRequestValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarServiceTest {

    @Mock
    private CarRepository carRepository;

    @Mock
    private CarMapper carMapper;

    @Mock
    private CarRequestValidator carRequestValidator;

    @InjectMocks
    private CarService carService;

    private Car buildCar() {
        return Car.builder()
                .id(1L)
                .vin("1HGBH41JXMN109186")
                .brand("Honda")
                .model("Civic")
                .manufacturedYear(2023)
                .color("Blue")
                .fuelType(FuelType.GASOLINE)
                .transmission("Automatic")
                .basePrice(new BigDecimal("25000.00"))
                .status(CarStatus.AVAILABLE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private CarRequest buildRequest() {
        return CarRequest.builder()
                .vin("1HGBH41JXMN109186")
                .brand("Honda")
                .model("Civic")
                .manufacturedYear(2023)
                .color("Blue")
                .fuelType(FuelType.GASOLINE)
                .transmission("Automatic")
                .basePrice(new BigDecimal("25000.00"))
                .status(CarStatus.AVAILABLE)
                .build();
    }

    private CarResponse buildResponse() {
        return CarResponse.builder()
                .id(1L)
                .vin("1HGBH41JXMN109186")
                .brand("Honda")
                .model("Civic")
                .manufacturedYear(2023)
                .color("Blue")
                .fuelType(FuelType.GASOLINE)
                .transmission("Automatic")
                .basePrice(new BigDecimal("25000.00"))
                .status(CarStatus.AVAILABLE)
                .build();
    }

    // --- getById tests ---

    @Test
    void givenExistingId_whenGetById_thenReturnsCarResponse() {
        Car car = buildCar();
        CarResponse expected = buildResponse();
        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        when(carMapper.toResponse(car)).thenReturn(expected);

        CarResponse result = carService.getById(1L);

        assertThat(result).isEqualTo(expected);
        verify(carRepository).findById(1L);
    }

    @Test
    void givenNonExistingId_whenGetById_thenThrowsCarNotFoundException() {
        when(carRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> carService.getById(999L))
                .isInstanceOf(CarNotFoundException.class)
                .hasMessageContaining("999");
    }

    // --- getAll tests ---

    @Test
    @SuppressWarnings("unchecked")
    void givenFilters_whenGetAll_thenDelegatesToRepository() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Car> carPage = new PageImpl<>(List.of(buildCar()));
        Page<CarResponse> responsePage = new PageImpl<>(List.of(buildResponse()));

        when(carRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(carPage);
        when(carMapper.toResponsePage(carPage)).thenReturn(responsePage);

        Page<CarResponse> result = carService.getAll(
                CarStatus.AVAILABLE, "Honda", new BigDecimal("10000"), new BigDecimal("50000"), pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(carRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @SuppressWarnings("unchecked")
    void givenNoFilters_whenGetAll_thenReturnsAllCars() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Car> carPage = new PageImpl<>(List.of(buildCar()));
        Page<CarResponse> responsePage = new PageImpl<>(List.of(buildResponse()));

        when(carRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(carPage);
        when(carMapper.toResponsePage(carPage)).thenReturn(responsePage);

        Page<CarResponse> result = carService.getAll(null, null, null, null, pageable);

        assertThat(result).isNotNull();
        verify(carRepository).findAll(any(Specification.class), eq(pageable));
    }

    // --- create tests ---

    @Test
    void givenValidAvailableCar_whenCreate_thenReturnsCarResponse() {
        CarRequest request = buildRequest();
        Car car = buildCar();
        CarResponse expected = buildResponse();

        when(carRepository.existsByVin(request.vin())).thenReturn(false);
        when(carMapper.toEntity(request)).thenReturn(car);
        when(carRepository.save(car)).thenReturn(car);
        when(carMapper.toResponse(car)).thenReturn(expected);

        CarResponse result = carService.create(request);

        assertThat(result).isEqualTo(expected);
        verify(carRepository).save(car);
    }

    @Test
    void givenReservedCarWithSellingPrice_whenCreate_thenSucceeds() {
        CarRequest request = CarRequest.builder()
                .vin("1HGBH41JXMN109186").brand("Honda").model("Civic")
                .manufacturedYear(2023).color("Blue").fuelType(FuelType.GASOLINE)
                .transmission("Automatic").basePrice(new BigDecimal("25000.00"))
                .status(CarStatus.RESERVED).sellingPrice(new BigDecimal("24000.00")).build();

        Car car = buildCar();
        car.setStatus(CarStatus.RESERVED);
        car.setSellingPrice(new BigDecimal("24000.00"));

        CarResponse expected = CarResponse.builder()
                .id(1L).vin("1HGBH41JXMN109186").brand("Honda").model("Civic")
                .manufacturedYear(2023).color("Blue").fuelType(FuelType.GASOLINE)
                .transmission("Automatic").basePrice(new BigDecimal("25000.00"))
                .status(CarStatus.RESERVED).sellingPrice(new BigDecimal("24000.00")).build();

        when(carRepository.existsByVin(request.vin())).thenReturn(false);
        when(carMapper.toEntity(request)).thenReturn(car);
        when(carRepository.save(car)).thenReturn(car);
        when(carMapper.toResponse(car)).thenReturn(expected);

        CarResponse result = carService.create(request);

        assertThat(result.status()).isEqualTo(CarStatus.RESERVED);
        assertThat(result.sellingPrice()).isEqualTo(new BigDecimal("24000.00"));
    }

    @Test
    void givenSoldCarWithSellingPrice_whenCreate_thenSucceeds() {
        CarRequest request = CarRequest.builder()
                .vin("1HGBH41JXMN109186").brand("Honda").model("Civic")
                .manufacturedYear(2023).color("Blue").fuelType(FuelType.GASOLINE)
                .transmission("Automatic").basePrice(new BigDecimal("25000.00"))
                .status(CarStatus.SOLD).sellingPrice(new BigDecimal("23000.00")).build();

        Car car = buildCar();
        CarResponse expected = buildResponse();

        when(carRepository.existsByVin(request.vin())).thenReturn(false);
        when(carMapper.toEntity(request)).thenReturn(car);
        when(carRepository.save(car)).thenReturn(car);
        when(carMapper.toResponse(car)).thenReturn(expected);

        CarResponse result = carService.create(request);

        assertThat(result).isNotNull();
    }

    @Test
    void givenDuplicateVin_whenCreate_thenThrowsDuplicateVinException() {
        CarRequest request = buildRequest();
        when(carRepository.existsByVin(request.vin())).thenReturn(true);

        assertThatThrownBy(() -> carService.create(request))
                .isInstanceOf(DuplicateVinException.class)
                .hasMessageContaining(request.vin());
    }

    @Test
    void givenNullStatus_whenCreate_thenDefaultsToAvailable() {
        CarRequest request = CarRequest.builder()
                .vin("1HGBH41JXMN109186").brand("Honda").model("Civic")
                .manufacturedYear(2023).color("Blue").fuelType(FuelType.GASOLINE)
                .transmission("Automatic").basePrice(new BigDecimal("25000.00"))
                .status(null).build();

        Car car = buildCar();
        CarResponse expected = buildResponse();

        when(carRepository.existsByVin(request.vin())).thenReturn(false);
        when(carMapper.toEntity(request)).thenReturn(car);
        when(carRepository.save(car)).thenReturn(car);
        when(carMapper.toResponse(car)).thenReturn(expected);

        CarResponse result = carService.create(request);

        assertThat(result).isNotNull();
    }

    // --- update tests ---

    @Test
    void givenValidRequest_whenUpdate_thenReturnsUpdatedCarResponse() {
        Car existingCar = buildCar();
        CarRequest request = buildRequest();
        CarResponse expected = buildResponse();

        when(carRepository.findById(1L)).thenReturn(Optional.of(existingCar));
        when(carRepository.save(existingCar)).thenReturn(existingCar);
        when(carMapper.toResponse(existingCar)).thenReturn(expected);

        CarResponse result = carService.update(1L, request);

        assertThat(result).isEqualTo(expected);
        verify(carMapper).updateEntity(existingCar, request);
    }

    @Test
    void givenNonExistingId_whenUpdate_thenThrowsCarNotFoundException() {
        when(carRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> carService.update(999L, buildRequest()))
                .isInstanceOf(CarNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void givenChangedVinToDuplicate_whenUpdate_thenThrowsDuplicateVinException() {
        Car existingCar = buildCar();
        CarRequest request = CarRequest.builder()
                .vin("3FADP4AJ5CM123456").brand("Honda").model("Civic")
                .manufacturedYear(2023).color("Blue").fuelType(FuelType.GASOLINE)
                .transmission("Automatic").basePrice(new BigDecimal("25000.00"))
                .status(CarStatus.AVAILABLE).build();

        when(carRepository.findById(1L)).thenReturn(Optional.of(existingCar));
        when(carRepository.existsByVinAndIdNot("3FADP4AJ5CM123456", 1L)).thenReturn(true);

        assertThatThrownBy(() -> carService.update(1L, request))
                .isInstanceOf(DuplicateVinException.class);
    }

    @Test
    void givenSameVin_whenUpdate_thenDoesNotCheckDuplicate() {
        Car existingCar = buildCar();
        CarRequest request = buildRequest();
        CarResponse expected = buildResponse();

        when(carRepository.findById(1L)).thenReturn(Optional.of(existingCar));
        when(carRepository.save(existingCar)).thenReturn(existingCar);
        when(carMapper.toResponse(existingCar)).thenReturn(expected);

        carService.update(1L, request);

        verify(carRepository, never()).existsByVinAndIdNot(any(), any());
    }


    @Test
    void givenExistingId_whenDelete_thenDeletesSuccessfully() {
        Car car = buildCar();
        when(carRepository.findById(1L)).thenReturn(Optional.of(car));

        carService.delete(1L);

        verify(carRepository).delete(car);
    }

    @Test
    void givenNonExistingId_whenDelete_thenThrowsCarNotFoundException() {
        when(carRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> carService.delete(999L))
                .isInstanceOf(CarNotFoundException.class)
                .hasMessageContaining("999");
    }
}
