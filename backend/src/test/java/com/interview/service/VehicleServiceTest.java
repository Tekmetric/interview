package com.interview.service;

import com.interview.domain.Vehicle;
import com.interview.domain.VehicleType;
import com.interview.dto.UpsertVehicleDto;
import com.interview.dto.VehicleDto;
import com.interview.dto.search.Direction;
import com.interview.dto.search.FieldName;
import com.interview.dto.search.PageRequestDto;
import com.interview.dto.search.PageResponseDto;
import com.interview.dto.search.SortBy;
import com.interview.dto.search.VehicleSearchCriteriaDto;
import com.interview.dto.search.VehicleSearchDto;
import com.interview.exception.DuplicateException;
import com.interview.exception.ErrorCode;
import com.interview.exception.NotFoundException;
import com.interview.filter.VehicleQueryFilter;
import com.interview.repository.VehicleRepository;
import com.interview.repository.VehicleSpecificationBuilder;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.interview.exception.ErrorCode.VEHICLE_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;
    @Mock
    private VehicleSpecificationBuilder vehicleSpecificationBuilder;

    @InjectMocks
    private VehicleService vehicleService;

    @Nested
    class GetByIdTests {
        @Test
        public void getById_ResourceDoesNotExist_NotFoundException() {
            // Given
            final long id = 1L;

            final Specification<Vehicle> mockedSpecification = mock(Specification.class);
            when(vehicleSpecificationBuilder.buildSpecification(any())).thenReturn(mockedSpecification);
            when(vehicleRepository.findOne(mockedSpecification)).thenReturn(Optional.empty());

            // When
            assertThatThrownBy(() -> vehicleService.getById(id))
                    // Then
                    .isInstanceOf(NotFoundException.class)
                    .matches(exception -> VEHICLE_NOT_FOUND.equals(((NotFoundException) exception).getErrorCode()));

            verify(vehicleSpecificationBuilder).buildSpecification(any());
            verify(vehicleRepository).findOne(mockedSpecification);
        }

        @Test
        public void getById_ResourceExists_Resource() {
            // Given
            final long id = 1L;
            final Vehicle vehicle = generateVehicle();

            final Specification<Vehicle> mockedSpecification = mock(Specification.class);

            when(vehicleSpecificationBuilder.buildSpecification(any())).thenReturn(mockedSpecification);
            when(vehicleRepository.findOne(mockedSpecification)).thenReturn(Optional.of(vehicle));

            // When
            VehicleDto result = vehicleService.getById(id);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(vehicle.getId());
            assertThat(result.make()).isEqualTo(vehicle.getMake());
            assertThat(result.model()).isEqualTo(vehicle.getModel());
            assertThat(result.vin()).isEqualTo(vehicle.getVin());
            assertThat(result.productionYear()).isEqualTo(vehicle.getProductionYear());
            assertThat(result.type()).isEqualTo(vehicle.getType());

            verify(vehicleSpecificationBuilder).buildSpecification(any());
            verify(vehicleRepository).findOne(mockedSpecification);
        }

    }

    @Nested
    class CreateTests {
        @Test
        public void create_ResourceWithVinAlreadyExists_DuplicateException() {
            // Given
            final UpsertVehicleDto vehicleToCreate = generateUpsertVehicleDto();

            final Specification<Vehicle> mockedSpecification = mock(Specification.class);
            final ArgumentCaptor<VehicleQueryFilter> vehicleQueryFilterCaptor = ArgumentCaptor.forClass(VehicleQueryFilter.class);

            when(vehicleSpecificationBuilder.buildSpecification(any())).thenReturn(mockedSpecification);
            when(vehicleRepository.exists(mockedSpecification)).thenReturn(true);

            // When
            assertThatThrownBy(() -> vehicleService.create(vehicleToCreate))
                    // Then
                    .isInstanceOf(DuplicateException.class)
                    .matches(exception -> ErrorCode.DUPLICATE_VEHICLE_VIN.equals(((DuplicateException) exception).getErrorCode()));

            verify(vehicleSpecificationBuilder).buildSpecification(vehicleQueryFilterCaptor.capture());
            verify(vehicleRepository).exists(mockedSpecification);

            assertThat(vehicleQueryFilterCaptor.getValue().includingVins()).containsExactly(vehicleToCreate.vin());
        }

        @Test
        public void create_ResourceWithVinDoesntExist_VehicleDto() {
            // Given
            final UpsertVehicleDto vehicleToCreate = generateUpsertVehicleDto();
            final Vehicle savedVehicle = generateVehicle();

            final Specification<Vehicle> mockedSpecification = mock(Specification.class);
            final ArgumentCaptor<VehicleQueryFilter> vehicleQueryFilterCaptor = ArgumentCaptor.forClass(VehicleQueryFilter.class);

            when(vehicleSpecificationBuilder.buildSpecification(any())).thenReturn(mockedSpecification);
            when(vehicleRepository.exists(mockedSpecification)).thenReturn(false);
            when(vehicleRepository.save(any())).thenReturn(savedVehicle);

            // When
            final VehicleDto result = vehicleService.create(vehicleToCreate);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(savedVehicle.getId());
            assertThat(result.vin()).isEqualTo(savedVehicle.getVin());
            assertThat(result.make()).isEqualTo(savedVehicle.getMake());
            assertThat(result.model()).isEqualTo(savedVehicle.getModel());
            assertThat(result.productionYear()).isEqualTo(savedVehicle.getProductionYear());
            assertThat(result.type()).isEqualTo(savedVehicle.getType());

            verify(vehicleSpecificationBuilder).buildSpecification(vehicleQueryFilterCaptor.capture());
            verify(vehicleRepository).exists(mockedSpecification);

            assertThat(vehicleQueryFilterCaptor.getValue().includingVins()).containsExactly(vehicleToCreate.vin());
        }
    }

    @Nested
    class UpdateTests {
        @Test
        public void update_ResourceDoesntExist_VehicleDto() {
            // Given
            final UpsertVehicleDto vehicleToUpdate = generateUpsertVehicleDto();
            final Vehicle existingVehicle = generateVehicle();

            final Specification<Vehicle> mockedSpecification = mock(Specification.class);
            final ArgumentCaptor<VehicleQueryFilter> vehicleQueryFilterCaptor = ArgumentCaptor.forClass(VehicleQueryFilter.class);

            when(vehicleSpecificationBuilder.buildSpecification(any())).thenReturn(mockedSpecification);
            when(vehicleRepository.findOne(mockedSpecification)).thenReturn(Optional.empty());

            // When
            assertThatThrownBy(() -> vehicleService.update(existingVehicle.getId(), vehicleToUpdate))
                    // Then
                    .isInstanceOf(NotFoundException.class)
                    .matches(exception -> VEHICLE_NOT_FOUND.equals(((NotFoundException) exception).getErrorCode()));

            verify(vehicleSpecificationBuilder).buildSpecification(vehicleQueryFilterCaptor.capture());
            verify(vehicleRepository).findOne(mockedSpecification);
            verifyNoMoreInteractions(vehicleRepository);
        }

        @Test
        public void update_ResourceWithVinAlreadyExists_DuplicateException() {
            // Given
            final UpsertVehicleDto vehicleToUpdate = generateUpsertVehicleDto();
            final Vehicle existingVehicle = generateVehicle();

            final Specification<Vehicle> mockedSpecification = mock(Specification.class);
            final ArgumentCaptor<VehicleQueryFilter> vehicleQueryFilterCaptor = ArgumentCaptor.forClass(VehicleQueryFilter.class);

            when(vehicleSpecificationBuilder.buildSpecification(any())).thenReturn(mockedSpecification);
            when(vehicleRepository.findOne(mockedSpecification)).thenReturn(Optional.of(existingVehicle));
            when(vehicleRepository.exists(mockedSpecification)).thenReturn(true);

            // When
            assertThatThrownBy(() -> vehicleService.update(existingVehicle.getId(), vehicleToUpdate))
                    // Then
                    .isInstanceOf(DuplicateException.class)
                    .matches(exception -> ErrorCode.DUPLICATE_VEHICLE_VIN.equals(((DuplicateException) exception).getErrorCode()));

            verify(vehicleSpecificationBuilder, times(2)).buildSpecification(vehicleQueryFilterCaptor.capture());
            verify(vehicleRepository).exists(mockedSpecification);

            assertThat(vehicleQueryFilterCaptor.getAllValues().get(1).includingVins()).containsExactly(vehicleToUpdate.vin());
            assertThat(vehicleQueryFilterCaptor.getAllValues().get(1).excludingIds()).containsExactly(existingVehicle.getId());
        }

        @Test
        public void update_ResourceWithVinDoesntExists_VehicleDto() {
            // Given
            final UpsertVehicleDto vehicleToUpdate = generateUpsertVehicleDto();
            final Vehicle existingVehicle = generateVehicle();

            final Specification<Vehicle> mockedSpecification = mock(Specification.class);
            final ArgumentCaptor<VehicleQueryFilter> vehicleQueryFilterCaptor = ArgumentCaptor.forClass(VehicleQueryFilter.class);

            when(vehicleSpecificationBuilder.buildSpecification(any())).thenReturn(mockedSpecification);
            when(vehicleRepository.findOne(mockedSpecification)).thenReturn(Optional.of(existingVehicle));
            when(vehicleRepository.exists(mockedSpecification)).thenReturn(false);
            when(vehicleRepository.save(any())).thenReturn(existingVehicle);

            // When
            final VehicleDto result = vehicleService.update(existingVehicle.getId(), vehicleToUpdate);

            // Then
            verify(vehicleSpecificationBuilder, times(2)).buildSpecification(vehicleQueryFilterCaptor.capture());
            verify(vehicleRepository).findOne(mockedSpecification);
            verify(vehicleRepository).exists(mockedSpecification);
            verify(vehicleRepository).save(any());

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(existingVehicle.getId());
            assertThat(result.vin()).isEqualTo(existingVehicle.getVin());
            assertThat(result.make()).isEqualTo(existingVehicle.getMake());
            assertThat(result.model()).isEqualTo(existingVehicle.getModel());
            assertThat(result.productionYear()).isEqualTo(existingVehicle.getProductionYear());
            assertThat(result.type()).isEqualTo(existingVehicle.getType());

            assertThat(vehicleQueryFilterCaptor.getAllValues().get(1).includingVins()).containsExactly(vehicleToUpdate.vin());
            assertThat(vehicleQueryFilterCaptor.getAllValues().get(1).excludingIds()).containsExactly(existingVehicle.getId());
        }

        @Test
        public void update_UpdateDataHasSameVin_VehicleDto() {
            // Given
            final Vehicle existingVehicle = generateVehicle();
            final UpsertVehicleDto vehicleToUpdate = generateUpsertVehicleDtoBuilder()
                    .vin(existingVehicle.getVin()).build();

            final Specification<Vehicle> mockedSpecification = mock(Specification.class);
            final ArgumentCaptor<VehicleQueryFilter> vehicleQueryFilterCaptor = ArgumentCaptor.forClass(VehicleQueryFilter.class);

            when(vehicleSpecificationBuilder.buildSpecification(any())).thenReturn(mockedSpecification);
            when(vehicleRepository.findOne(mockedSpecification)).thenReturn(Optional.of(existingVehicle));
            when(vehicleRepository.save(any())).thenReturn(existingVehicle);

            // When
            final VehicleDto result = vehicleService.update(existingVehicle.getId(), vehicleToUpdate);

            // Then
            verify(vehicleSpecificationBuilder).buildSpecification(vehicleQueryFilterCaptor.capture());
            verify(vehicleRepository).findOne(mockedSpecification);
            verify(vehicleRepository).save(any());

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(existingVehicle.getId());
            assertThat(result.vin()).isEqualTo(existingVehicle.getVin());
            assertThat(result.make()).isEqualTo(existingVehicle.getMake());
            assertThat(result.model()).isEqualTo(existingVehicle.getModel());
            assertThat(result.productionYear()).isEqualTo(existingVehicle.getProductionYear());
            assertThat(result.type()).isEqualTo(existingVehicle.getType());

            assertThat(vehicleQueryFilterCaptor.getValue().includingIds()).containsExactly(existingVehicle.getId());
        }
    }

    @Nested
    class DeleteTests {
        @Test
        public void delete_ExistingId_VehicleDto() {
            // Given
            final Specification<Vehicle> mockedSpecification = mock(Specification.class);
            final ArgumentCaptor<VehicleQueryFilter> vehicleQueryFilterCaptor = ArgumentCaptor.forClass(VehicleQueryFilter.class);

            when(vehicleSpecificationBuilder.buildSpecification(any())).thenReturn(mockedSpecification);
            when(vehicleRepository.delete(mockedSpecification)).thenReturn(1L);

            // When
            assertThatCode(() -> vehicleService.delete(1L))
                    // Then
                    .doesNotThrowAnyException();

            verify(vehicleSpecificationBuilder).buildSpecification(vehicleQueryFilterCaptor.capture());
            verify(vehicleRepository).delete(mockedSpecification);

            assertThat(vehicleQueryFilterCaptor.getValue().includingIds()).containsExactly(1L);
        }
    }

    @Nested
    class SearchTests {
        @Test
        public void search_NoResults_EmptyResult() {
            // Given
            final VehicleSearchDto searchDto = VehicleSearchDto.builder()
                    .searchCriteriaDto(VehicleSearchCriteriaDto.builder()
                            .includingVehicleTypes(Set.of(VehicleType.SEDAN, VehicleType.SUV))
                            .productionYearTo("2014")
                            .build())
                    .pageRequestDto(PageRequestDto.builder()
                            .pageNumber(0)
                            .pageSize(10)
                            .sortBy(List.of(SortBy.of(FieldName.CREATED_AT, Direction.ASC))).build())
                    .build();

            final Specification<Vehicle> mockedSpecification = mock(Specification.class);
            final ArgumentCaptor<VehicleQueryFilter> vehicleQueryFilterCaptor = ArgumentCaptor.forClass(VehicleQueryFilter.class);

            when(vehicleSpecificationBuilder.buildSpecification(any())).thenReturn(mockedSpecification);
            when(vehicleRepository.findAll(eq(mockedSpecification), any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));

            // When
            final PageResponseDto<VehicleDto> result = vehicleService.search(searchDto);

            verify(vehicleSpecificationBuilder).buildSpecification(vehicleQueryFilterCaptor.capture());
            verify(vehicleRepository).findAll(eq(mockedSpecification), any(Pageable.class));

            assertThat(result).isNotNull();
            assertThat(result.content()).isEmpty();

            assertThat(vehicleQueryFilterCaptor.getValue().includingVehicleTypes()).containsExactlyInAnyOrder(VehicleType.SEDAN, VehicleType.SUV);
            assertThat(vehicleQueryFilterCaptor.getValue().productionYearTo()).isEqualTo(Year.of(2014));
        }

        @Test
        public void search_ExistingSearchResult_VehicleDtos() {
            // Given
            final VehicleSearchDto searchDto = VehicleSearchDto.builder()
                    .searchCriteriaDto(VehicleSearchCriteriaDto.builder()
                            .includingVehicleTypes(Set.of(VehicleType.SEDAN, VehicleType.SUV))
                            .productionYearTo("2014")
                            .build())
                    .pageRequestDto(PageRequestDto.builder()
                            .pageNumber(0)
                            .pageSize(10)
                            .sortBy(List.of(SortBy.of(FieldName.CREATED_AT, Direction.ASC))).build())
                    .build();

            final Specification<Vehicle> mockedSpecification = mock(Specification.class);
            final ArgumentCaptor<VehicleQueryFilter> vehicleQueryFilterCaptor = ArgumentCaptor.forClass(VehicleQueryFilter.class);

            when(vehicleSpecificationBuilder.buildSpecification(any())).thenReturn(mockedSpecification);
            when(vehicleRepository.findAll(eq(mockedSpecification), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(generateVehicle())));

            // When
            final PageResponseDto<VehicleDto> result = vehicleService.search(searchDto);

            verify(vehicleSpecificationBuilder).buildSpecification(vehicleQueryFilterCaptor.capture());
            verify(vehicleRepository).findAll(eq(mockedSpecification), any(Pageable.class));

            assertThat(result).isNotNull();
            assertThat(result.content()).hasSize(1);

            assertThat(vehicleQueryFilterCaptor.getValue().includingVehicleTypes()).containsExactlyInAnyOrder(VehicleType.SEDAN, VehicleType.SUV);
            assertThat(vehicleQueryFilterCaptor.getValue().productionYearTo()).isEqualTo(Year.of(2014));
        }
    }

    private static Vehicle generateVehicle() {
        final Vehicle vehicle = Vehicle.builder()
                .make("Toyota")
                .model("Corolla")
                .vin("1HGBH41JXMN109186")
                .productionYear(Year.of(2020))
                .type(VehicleType.SEDAN)
                .build();

        ReflectionTestUtils.setField(vehicle, "id", 15L);
        return vehicle;
    }

    private static UpsertVehicleDto generateUpsertVehicleDto() {
        return generateUpsertVehicleDtoBuilder().build();
    }

    private static UpsertVehicleDto.UpsertVehicleDtoBuilder generateUpsertVehicleDtoBuilder() {
        return UpsertVehicleDto.builder()
                .make("Toyota")
                .model("Corolla")
                .vin("1HGBH41JXMN109187")
                .productionYear("2020")
                .type(VehicleType.SEDAN);
    }

}
