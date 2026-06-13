package com.interview.validator;

import com.interview.dto.CarRequest;
import com.interview.exception.InvalidCarDataException;
import com.interview.model.CarStatus;
import com.interview.model.FuelType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CarRequestValidatorTest {

    private final CarRequestValidator validator = new CarRequestValidator();

    private CarRequest buildRequest(CarStatus status, BigDecimal sellingPrice, Integer manufacturedYear) {
        return CarRequest.builder()
                .vin("1HGBH41JXMN109186")
                .brand("Honda")
                .model("Civic")
                .manufacturedYear(manufacturedYear)
                .color("Blue")
                .fuelType(FuelType.GASOLINE)
                .transmission("Automatic")
                .basePrice(new BigDecimal("25000.00"))
                .sellingPrice(sellingPrice)
                .status(status)
                .build();
    }

    @Test
    void givenAvailableStatusWithoutSellingPrice_whenValidate_thenNoException() {
        CarRequest request = buildRequest(CarStatus.AVAILABLE, null, 2023);

        assertThatNoException().isThrownBy(() -> validator.validate(request));
    }

    @Test
    void givenNullStatus_whenValidate_thenTreatedAsAvailableAndNoException() {
        CarRequest request = buildRequest(null, null, 2023);

        assertThatNoException().isThrownBy(() -> validator.validate(request));
    }

    @Test
    void givenReservedStatusWithSellingPrice_whenValidate_thenNoException() {
        CarRequest request = buildRequest(CarStatus.RESERVED, new BigDecimal("24000.00"), 2023);

        assertThatNoException().isThrownBy(() -> validator.validate(request));
    }

    @Test
    void givenSoldStatusWithSellingPrice_whenValidate_thenNoException() {
        CarRequest request = buildRequest(CarStatus.SOLD, new BigDecimal("23000.00"), 2023);

        assertThatNoException().isThrownBy(() -> validator.validate(request));
    }

    @Test
    void givenReservedStatusWithoutSellingPrice_whenValidate_thenThrowsInvalidCarDataException() {
        CarRequest request = buildRequest(CarStatus.RESERVED, null, 2023);

        assertThatThrownBy(() -> validator.validate(request))
                .isInstanceOf(InvalidCarDataException.class)
                .hasMessageContaining("Selling price is required when status is RESERVED");
    }

    @Test
    void givenSoldStatusWithoutSellingPrice_whenValidate_thenThrowsInvalidCarDataException() {
        CarRequest request = buildRequest(CarStatus.SOLD, null, 2023);

        assertThatThrownBy(() -> validator.validate(request))
                .isInstanceOf(InvalidCarDataException.class)
                .hasMessageContaining("Selling price is required when status is SOLD");
    }

    @Test
    void givenAvailableStatusWithSellingPrice_whenValidate_thenThrowsInvalidCarDataException() {
        CarRequest request = buildRequest(CarStatus.AVAILABLE, new BigDecimal("24000.00"), 2023);

        assertThatThrownBy(() -> validator.validate(request))
                .isInstanceOf(InvalidCarDataException.class)
                .hasMessageContaining("Selling price must be null when status is AVAILABLE");
    }

    @Test
    void givenManufacturedYearInFuture_whenValidate_thenThrowsInvalidCarDataException() {
        CarRequest request = buildRequest(CarStatus.AVAILABLE, new BigDecimal("24000.00"), 2060);

        assertThatThrownBy(() -> validator.validate(request))
            .isInstanceOf(InvalidCarDataException.class)
            .hasMessageContaining("Selling price must be null when status is AVAILABLE");
    }
}
