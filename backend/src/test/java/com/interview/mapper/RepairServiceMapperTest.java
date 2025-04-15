package com.interview.mapper;

import com.interview.db.entity.RepairService;
import com.interview.dto.RepairServiceDTO;
import com.interview.dto.RepairServiceStatus;
import com.interview.exception.MappingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RepairServiceMapperTest {
    private final Long testId = 1L;

    private RepairServiceMapper mapper;
    private RepairService repairService;
    private RepairServiceDTO repairServiceDTO;

    @BeforeEach
    void setUp() {
        mapper = new RepairServiceMapper();

        // Set up test entity
        repairService = RepairService.builder()
                .id(testId)
                .customerName("Test Customer")
                .customerPhone("1234567890")
                .vehicleMake("Test Make")
                .vehicleModel("Test Model")
                .vehicleYear(2020)
                .licensePlate("TEST123")
                .serviceDescription("Test Service")
                .odometerReading(10000)
                .status("PENDING")
                .build();

        // Set up test DTO
        repairServiceDTO = RepairServiceDTO.builder()
                .id(testId)
                .customerName("Test Customer")
                .customerPhone("1234567890")
                .vehicleMake("Test Make")
                .vehicleModel("Test Model")
                .vehicleYear(2020)
                .licensePlate("TEST123")
                .serviceDescription("Test Service")
                .odometerReading(10000)
                .status(RepairServiceStatus.PENDING)
                .build();
    }

    @Test
    void toDto_WhenEntityIsNull_ShouldReturnNull() {
        // Act
        var result = mapper.toDto(null);

        // Assert
        assertNull(result);
    }

    @Test
    void toDto_WhenEntityIsValid_ShouldMapCorrectly() {
        // Act
        var result = mapper.toDto(repairService);

        // Assert
        assertNotNull(result);
        assertEquals(repairService.getId(), result.getId());
        assertEquals(repairService.getCustomerName(), result.getCustomerName());
        assertEquals(repairService.getCustomerPhone(), result.getCustomerPhone());
        assertEquals(repairService.getVehicleMake(), result.getVehicleMake());
        assertEquals(repairService.getVehicleModel(), result.getVehicleModel());
        assertEquals(repairService.getVehicleYear(), result.getVehicleYear());
        assertEquals(repairService.getLicensePlate(), result.getLicensePlate());
        assertEquals(repairService.getServiceDescription(), result.getServiceDescription());
        assertEquals(repairService.getOdometerReading(), result.getOdometerReading());
        assertEquals(RepairServiceStatus.PENDING, result.getStatus());
    }

    @Test
    void toEntity_WhenDtoIsNull_ShouldReturnNull() {
        // Act
        var result = mapper.toEntity(null);

        // Assert
        assertNull(result);
    }

    @Test
    void toEntity_WhenDtoIsValid_ShouldMapCorrectly() {
        // Act
        var result = mapper.toEntity(repairServiceDTO);

        // Assert
        assertNotNull(result);
        assertEquals(repairServiceDTO.getId(), result.getId());
        assertEquals(repairServiceDTO.getCustomerName(), result.getCustomerName());
        assertEquals(repairServiceDTO.getCustomerPhone(), result.getCustomerPhone());
        assertEquals(repairServiceDTO.getVehicleMake(), result.getVehicleMake());
        assertEquals(repairServiceDTO.getVehicleModel(), result.getVehicleModel());
        assertEquals(repairServiceDTO.getVehicleYear(), result.getVehicleYear());
        assertEquals(repairServiceDTO.getLicensePlate(), result.getLicensePlate());
        assertEquals(repairServiceDTO.getServiceDescription(), result.getServiceDescription());
        assertEquals(repairServiceDTO.getOdometerReading(), result.getOdometerReading());
        assertEquals(repairServiceDTO.getStatus().name(), result.getStatus());
    }

    @Test
    void mapToStatusEnum_WhenStatusStringIsNull_ShouldReturnNull() {
        // Arrange
        var serviceWithNullStatus = RepairService.builder()
                .id(testId)
                .status(null)
                .build();

        // Act
        var result = mapper.toDto(serviceWithNullStatus);

        // Assert
        assertNotNull(result);
        assertNull(result.getStatus());
    }

    @Test
    void mapToStatusEnum_WhenStatusStringIsInvalid_ShouldThrowException() {
        // Arrange
        var serviceWithInvalidStatus = RepairService.builder()
                .id(testId)
                .status("INVALID_STATUS")
                .build();

        // Act & Assert
        assertThrows(MappingException.class, () -> mapper.toDto(serviceWithInvalidStatus));
    }

    @Test
    void mapToStatusString_WhenStatusEnumIsNull_ShouldReturnNull() {
        // Arrange
        var dtoWithNullStatus = RepairServiceDTO.builder()
                .id(testId)
                .status(null)
                .build();

        // Act
        var result = mapper.toEntity(dtoWithNullStatus);

        // Assert
        assertNotNull(result);
        assertNull(result.getStatus());
    }

    @Test
    void toDto_WithNullableServiceDescription_ShouldMapCorrectly() {
        // Arrange
        var serviceWithNullDescription = RepairService.builder()
                .id(testId)
                .customerName("Test Customer")
                .customerPhone("1234567890")
                .vehicleMake("Test Make")
                .vehicleModel("Test Model")
                .vehicleYear(2020)
                .licensePlate("TEST123")
                .serviceDescription(null)
                .odometerReading(10000)
                .status("PENDING")
                .build();

        // Act
        var result = mapper.toDto(serviceWithNullDescription);

        // Assert
        assertNotNull(result);
        assertNull(result.getServiceDescription());
    }

    @Test
    void toEntity_WithNullableServiceDescription_ShouldMapCorrectly() {
        // Arrange
        var dtoWithNullDescription = RepairServiceDTO.builder()
                .id(testId)
                .customerName("Test Customer")
                .customerPhone("1234567890")
                .vehicleMake("Test Make")
                .vehicleModel("Test Model")
                .vehicleYear(2020)
                .licensePlate("TEST123")
                .serviceDescription(null)
                .odometerReading(10000)
                .status(RepairServiceStatus.PENDING)
                .build();

        // Act
        var result = mapper.toEntity(dtoWithNullDescription);

        // Assert
        assertNotNull(result);
        assertNull(result.getServiceDescription());
    }
}
