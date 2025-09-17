package com.interview.test.validation;

import com.interview.dto.VehicleResponse;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class VehicleValidator {

	public static void validateGeneratedFields(VehicleResponse vehicleResponse) {
		assertNotNull(vehicleResponse);
		assertNotNull(vehicleResponse.getId());
		TimestampValidator.validateRecentTimestamp(vehicleResponse.getCreatedAt());
		TimestampValidator.validateRecentTimestamp(vehicleResponse.getUpdatedAt());
	}
}
