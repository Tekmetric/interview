package com.interview.mapper;

import com.interview.domain.Vehicle;
import com.interview.domain.VehicleType;
import com.interview.dto.UpsertVehicleDto;
import com.interview.dto.VehicleDto;
import com.interview.dto.search.VehicleSearchCriteriaDto;
import com.interview.filter.VehicleQueryFilter;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.Year;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class VehicleMapperTest {

    @Test
    public void toQueryFilter_AllFieldsPopulated_CorrectlyMappedFilter() {
        // Given
        VehicleSearchCriteriaDto searchCriteria = VehicleSearchCriteriaDto.builder()
                .includingVehicleTypes(Set.of(VehicleType.SEDAN, VehicleType.SUV))
                .excludingVehicleTypes(Set.of(VehicleType.PICKUP))
                .productionYearFrom("2020")
                .productionYearTo("2023")
                .includingVins(Set.of("1HGBH41JXMN109186", "1HGBH41JXMN109187"))
                .excludingVins(Set.of("1HGBH41JXMN109188"))
                .build();

        // When
        VehicleQueryFilter result = VehicleMapper.toQueryFilter(searchCriteria);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.includingVehicleTypes()).containsExactlyInAnyOrder(VehicleType.SEDAN, VehicleType.SUV);
        assertThat(result.excludingVehicleTypes()).containsExactly(VehicleType.PICKUP);
        assertThat(result.productionYearFrom()).isEqualTo(Year.of(2020));
        assertThat(result.productionYearTo()).isEqualTo(Year.of(2023));
        assertThat(result.includingVins()).containsExactlyInAnyOrder("1HGBH41JXMN109186", "1HGBH41JXMN109187");
        assertThat(result.excludingVins()).containsExactly("1HGBH41JXMN109188");
    }

    @Test
    public void toQueryFilter_EmptyFields_FilterWithNullValues() {
        // Given
        VehicleSearchCriteriaDto searchCriteria = VehicleSearchCriteriaDto.builder()
                .includingVehicleTypes(null)
                .excludingVehicleTypes(null)
                .productionYearFrom(null)
                .productionYearTo(null)
                .includingVins(null)
                .excludingVins(null)
                .build();

        // When
        VehicleQueryFilter result = VehicleMapper.toQueryFilter(searchCriteria);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.includingVehicleTypes()).isNull();
        assertThat(result.excludingVehicleTypes()).isNull();
        assertThat(result.productionYearFrom()).isNull();
        assertThat(result.productionYearTo()).isNull();
        assertThat(result.includingVins()).isNull();
        assertThat(result.excludingVins()).isNull();
    }

    @Test
    public void toQueryFilter_PartialFields_CorrectlyMappedPartialFilter() {
        // Given
        VehicleSearchCriteriaDto searchCriteria = VehicleSearchCriteriaDto.builder()
                .includingVehicleTypes(Set.of(VehicleType.COUPE))
                .productionYearFrom("2015")
                .includingVins(Set.of("1HGBH41JXMN109186"))
                .build();

        // When
        VehicleQueryFilter result = VehicleMapper.toQueryFilter(searchCriteria);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.includingVehicleTypes()).containsExactly(VehicleType.COUPE);
        assertThat(result.excludingVehicleTypes()).isNull();
        assertThat(result.productionYearFrom()).isEqualTo(Year.of(2015));
        assertThat(result.productionYearTo()).isNull();
        assertThat(result.includingVins()).containsExactly("1HGBH41JXMN109186");
        assertThat(result.excludingVins()).isNull();
    }

    @Test
    public void toEntity_ValidUpsertDto_MappedVehicle() {
        // Given
        UpsertVehicleDto upsertDto = UpsertVehicleDto.builder()
                .type(VehicleType.SEDAN)
                .productionYear("2022")
                .vin("1HGBH41JXMN109186")
                .model("Corolla")
                .make("Toyota")
                .build();

        // When
        Vehicle result = VehicleMapper.toEntity(upsertDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo(VehicleType.SEDAN);
        assertThat(result.getProductionYear()).isEqualTo(Year.of(2022));
        assertThat(result.getVin()).isEqualTo("1HGBH41JXMN109186");
        assertThat(result.getModel()).isEqualTo("Corolla");
        assertThat(result.getMake()).isEqualTo("Toyota");

        assertThat(result.getId()).isNull();
    }

    @Test
    public void toEntity_DifferentVehicleType_MappedVehicle() {
        // Given
        UpsertVehicleDto upsertDto = UpsertVehicleDto.builder()
                .type(VehicleType.PICKUP)
                .productionYear("2019")
                .vin("1FTFW1ET5DFC12345")
                .model("F-150")
                .make("Ford")
                .build();

        // When
        Vehicle result = VehicleMapper.toEntity(upsertDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo(VehicleType.PICKUP);
        assertThat(result.getProductionYear()).isEqualTo(Year.of(2019));
        assertThat(result.getVin()).isEqualTo("1FTFW1ET5DFC12345");
        assertThat(result.getModel()).isEqualTo("F-150");
        assertThat(result.getMake()).isEqualTo("Ford");

        assertThat(result.getId()).isNull();
    }

    // Tests for toDto method
    @Test
    public void toDto_VehicleWithAllFields_CorrectlyMappedDto() {
        // Given
        Vehicle vehicle = Vehicle.builder()
                .type(VehicleType.SUV)
                .productionYear(Year.of(2021))
                .vin("1HGBH41JXMN109187")
                .model("CR-V")
                .make("Honda")
                .build();

        setPrivateField(vehicle, "id", 1L);
        setPrivateField(vehicle, "createdDate", Instant.parse("2023-01-15T10:30:00Z"));
        setPrivateField(vehicle, "lastModifiedDate", Instant.parse("2023-01-20T14:45:00Z"));
        setPrivateField(vehicle, "createdBy", "test-user");
        setPrivateField(vehicle, "lastModifiedBy", "test-admin");

        // When
        VehicleDto result = VehicleMapper.toDto(vehicle);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.type()).isEqualTo(VehicleType.SUV);
        assertThat(result.productionYear()).isEqualTo(Year.of(2021));
        assertThat(result.vin()).isEqualTo("1HGBH41JXMN109187");
        assertThat(result.model()).isEqualTo("CR-V");
        assertThat(result.make()).isEqualTo("Honda");
        assertThat(result.createdDate()).isEqualTo(vehicle.getCreatedDate());
        assertThat(result.lastModifiedDate()).isEqualTo(vehicle.getLastModifiedDate());
        assertThat(result.createdBy()).isEqualTo("test-user");
        assertThat(result.lastModifiedBy()).isEqualTo("test-admin");
    }

    @Test
    public void toDto_VehicleWithNullAuditFieldsAndId_CorrectlyMappedDtoWithNulls() {
        // Given
        Vehicle vehicle = Vehicle.builder()
                .type(VehicleType.COUPE)
                .productionYear(Year.of(2020))
                .vin("1G1YY22G965123456")
                .model("Camaro")
                .make("Chevrolet")
                .build();

        // When
        VehicleDto result = VehicleMapper.toDto(vehicle);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isNull();
        assertThat(result.type()).isEqualTo(VehicleType.COUPE);
        assertThat(result.productionYear()).isEqualTo(Year.of(2020));
        assertThat(result.vin()).isEqualTo("1G1YY22G965123456");
        assertThat(result.model()).isEqualTo("Camaro");
        assertThat(result.make()).isEqualTo("Chevrolet");
        assertThat(result.createdDate()).isNull();
        assertThat(result.lastModifiedDate()).isNull();
        assertThat(result.createdBy()).isNull();
        assertThat(result.lastModifiedBy()).isNull();
    }

    @Test
    public void toDto_VehicleWithMinimumData_CorrectlyMappedDto() {
        // Given
        Vehicle vehicle = Vehicle.builder()
                .type(VehicleType.HATCHBACK)
                .productionYear(Year.of(2018))
                .vin("JM1BL1SF1A1234567")
                .model("3")
                .make("Mazda")
                .build();

        setPrivateField(vehicle, "id", 1L);

        // When
        VehicleDto result = VehicleMapper.toDto(vehicle);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.type()).isEqualTo(VehicleType.HATCHBACK);
        assertThat(result.productionYear()).isEqualTo(Year.of(2018));
        assertThat(result.vin()).isEqualTo("JM1BL1SF1A1234567");
        assertThat(result.model()).isEqualTo("3");
        assertThat(result.make()).isEqualTo("Mazda");
    }

    private void setPrivateField(Object object, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = object.getClass().getSuperclass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(object, value);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }
}
