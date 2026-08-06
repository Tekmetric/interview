package com.interview.service;

import com.interview.dto.CarDto;
import com.interview.entity.CarEntity;
import com.interview.exception.CarNotFoundException;
import com.interview.exception.CarUpdateIdMismatchException;
import com.interview.exception.DuplicateCarException;
import com.interview.mapper.CarMapper;
import com.interview.repository.CarRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarServiceTest {

    @Mock
    private CarMapper carMapper;

    @Mock
    private CarRepository carRepository;

    @InjectMocks
    private CarService carService;

    @Nested
    class GetAllCars {

        @Test
        void returnsMappedDtos() {
            final var entity = new CarEntity();
            final var dto = buildCarDto(UUID.randomUUID());

            when(carRepository.findAll()).thenReturn(List.of(entity));
            when(carMapper.toDto(entity)).thenReturn(dto);

            final var result = carService.getAllCars();

            assertThat(result).containsExactly(dto);
        }
    }

    @Nested
    class GetCar {

        @Test
        void returnsMappedDto() {
            final var id = UUID.randomUUID();
            final var entity = new CarEntity();
            final var dto = buildCarDto(id);

            when(carRepository.findById(id)).thenReturn(Optional.of(entity));
            when(carMapper.toDto(entity)).thenReturn(dto);

            final var result = carService.getCar(id);

            assertThat(result).isEqualTo(dto);
        }

        @Test
        void throwsCarNotFoundExceptionWhenCarDoesNotExist() {
            final var id = UUID.randomUUID();

            when(carRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> carService.getCar(id))
                    .isInstanceOf(CarNotFoundException.class);
        }
    }

    @Nested
    class CreateCar {

        @Test
        void savesAndReturnsMappedDto() {
            final var id = UUID.randomUUID();
            final var inputDto = buildCarDto(null);
            final var entity = new CarEntity();
            final var savedEntity = new CarEntity();
            final var savedDto = buildCarDto(id);

            when(carMapper.toEntity(inputDto)).thenReturn(entity);
            when(carRepository.saveAndFlush(entity)).thenReturn(savedEntity);
            when(carMapper.toDto(savedEntity)).thenReturn(savedDto);

            final var result = carService.createCar(inputDto);

            assertThat(result).isEqualTo(savedDto);
            verify(carRepository).saveAndFlush(entity);
        }

        @Test
        void throwsDuplicateCarExceptionOnConstraintViolation() {
            final var inputDto = buildCarDto(null);
            final var entity = new CarEntity();

            when(carMapper.toEntity(inputDto)).thenReturn(entity);
            when(carRepository.saveAndFlush(entity)).thenThrow(new DataIntegrityViolationException("duplicate"));

            assertThatThrownBy(() -> carService.createCar(inputDto))
                    .isInstanceOf(DuplicateCarException.class);
        }
    }

    @Nested
    class UpdateCar {

        @Test
        void updatesWhenDtoIdIsNotNullAndReturnsMappedDto() {
            final var id = UUID.randomUUID();
            final var inputDto = buildCarDto(id);
            final var existingEntity = new CarEntity();
            final var updatedEntity = new CarEntity();
            final var updatedDto = buildCarDto(id);

            when(carRepository.findById(id)).thenReturn(Optional.of(existingEntity));
            when(carRepository.saveAndFlush(existingEntity)).thenReturn(updatedEntity);
            when(carMapper.toDto(updatedEntity)).thenReturn(updatedDto);

            final var result = carService.updateCar(id, inputDto);

            assertThat(result).isEqualTo(updatedDto);
            verify(carMapper).updateEntityFromDto(inputDto, existingEntity);
            verify(carRepository).saveAndFlush(existingEntity);
        }

        @Test
        void updatesWhenDtoIdNullAndReturnsMappedDto() {
            final var id = UUID.randomUUID();
            final var inputDto = buildCarDto(null);
            final var existingEntity = new CarEntity();
            final var updatedEntity = new CarEntity();
            final var updatedDto = buildCarDto(id);

            when(carRepository.findById(id)).thenReturn(Optional.of(existingEntity));
            when(carRepository.saveAndFlush(existingEntity)).thenReturn(updatedEntity);
            when(carMapper.toDto(updatedEntity)).thenReturn(updatedDto);

            final var result = carService.updateCar(id, inputDto);

            assertThat(result).isEqualTo(updatedDto);
            verify(carMapper).updateEntityFromDto(inputDto, existingEntity);
            verify(carRepository).saveAndFlush(existingEntity);
        }

        @Test
        void throwsCarNotFoundExceptionWhenCarDoesNotExist() {
            final var id = UUID.randomUUID();
            final var inputDto = buildCarDto(id);

            when(carRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> carService.updateCar(id, inputDto))
                    .isInstanceOf(CarNotFoundException.class);
        }

        @Test
        void throwsCarUpdateIdMismatchExceptionWhenIdsDiffer() {
            final var pathId = UUID.randomUUID();
            final var bodyId = UUID.randomUUID();
            final var inputDto = buildCarDto(bodyId);

            assertThatThrownBy(() -> carService.updateCar(pathId, inputDto))
                    .isInstanceOf(CarUpdateIdMismatchException.class);

            verifyNoInteractions(carRepository);
        }

        @Test
        void throwsDuplicateCarExceptionOnConstraintViolation() {
            final var id = UUID.randomUUID();
            final var inputDto = buildCarDto(null);
            final var existingEntity = new CarEntity();

            when(carRepository.findById(id)).thenReturn(Optional.of(existingEntity));
            when(carRepository.saveAndFlush(existingEntity)).thenThrow(new DataIntegrityViolationException("duplicate"));

            assertThatThrownBy(() -> carService.updateCar(id, inputDto))
                    .isInstanceOf(DuplicateCarException.class);
        }
    }

    @Nested
    class DeleteCar {

        @Test
        void deletesExistingCar() {
            final var id = UUID.randomUUID();
            final var entity = new CarEntity();

            when(carRepository.findById(id)).thenReturn(Optional.of(entity));

            carService.deleteCar(id);

            verify(carRepository).delete(entity);
        }

        @Test
        void doesNotThrowWhenCarDoesNotExist() {
            final var id = UUID.randomUUID();

            when(carRepository.findById(id)).thenReturn(Optional.empty());

            carService.deleteCar(id);

            verify(carRepository, never()).delete(any());
        }
    }

    private CarDto buildCarDto(final UUID id) {
        return new CarDto(id, "1HGBH41JXMN109186", 2020, "Acura", "TLX", "PMC", "Valencia Red Pearl", 33000, "Automatic", "AWD");
    }
}
