package com.interview.repository;

import com.interview.model.Car;
import com.interview.model.CarStatus;
import com.interview.model.FuelType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.sql.init.mode=always",
        "spring.sql.init.schema-locations=classpath:database/schema.sql",
        "spring.sql.init.data-locations=classpath:database/data.sql"
})
class CarRepositoryTest {

    @Autowired
    private CarRepository carRepository;

    @Test
    void givenExistingCar_whenFindById_thenReturnsCar() {
        Optional<Car> result = carRepository.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getVin()).isEqualTo("1HGBH41JXMN109186");
        assertThat(result.get().getBrand()).isEqualTo("Honda");
    }

    @Test
    void givenNonExistingCar_whenFindById_thenReturnsEmpty() {
        Optional<Car> result = carRepository.findById(999L);

        assertThat(result).isEmpty();
    }

    @Test
    void givenExistingVin_whenExistsByVin_thenReturnsTrue() {
        assertThat(carRepository.existsByVin("1HGBH41JXMN109186")).isTrue();
    }

    @Test
    void givenNonExistingVin_whenExistsByVin_thenReturnsFalse() {
        assertThat(carRepository.existsByVin("NON_EXISTING_VIN")).isFalse();
    }

    @Test
    void givenSameCarId_whenExistsByVinAndIdNot_thenReturnsFalse() {
        assertThat(carRepository.existsByVinAndIdNot("1HGBH41JXMN109186", 1L)).isFalse();
    }

    @Test
    void givenDifferentCarId_whenExistsByVinAndIdNot_thenReturnsTrue() {
        assertThat(carRepository.existsByVinAndIdNot("1HGBH41JXMN109186", 2L)).isTrue();
    }

    @Test
    void givenStatusSpecification_whenFindAll_thenFiltersCorrectly() {
        Specification<Car> spec = CarSpecification.hasStatus(CarStatus.AVAILABLE);
        Page<Car> result = carRepository.findAll(spec, PageRequest.of(0, 20));

        assertThat(result.getContent()).allMatch(car -> car.getStatus() == CarStatus.AVAILABLE);
        assertThat(result.getTotalElements()).isEqualTo(3);
    }

    @Test
    void givenBrandSpecification_whenFindAll_thenFiltersCorrectly() {
        Specification<Car> spec = CarSpecification.hasBrand("Honda");
        Page<Car> result = carRepository.findAll(spec, PageRequest.of(0, 20));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getBrand()).isEqualTo("Honda");
    }

    @Test
    void givenMinPriceSpecification_whenFindAll_thenFiltersCorrectly() {
        Specification<Car> spec = CarSpecification.hasBasePriceMin(new BigDecimal("40000.00"));
        Page<Car> result = carRepository.findAll(spec, PageRequest.of(0, 20));

        assertThat(result.getContent()).allMatch(
                car -> car.getBasePrice().compareTo(new BigDecimal("40000.00")) >= 0);
        assertThat(result.getTotalElements()).isEqualTo(3);
    }

    @Test
    void givenMaxPriceSpecification_whenFindAll_thenFiltersCorrectly() {
        Specification<Car> spec = CarSpecification.hasBasePriceMax(new BigDecimal("30000.00"));
        Page<Car> result = carRepository.findAll(spec, PageRequest.of(0, 20));

        assertThat(result.getContent()).allMatch(
                car -> car.getBasePrice().compareTo(new BigDecimal("30000.00")) <= 0);
        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @Test
    void givenPriceRangeSpecification_whenFindAll_thenFiltersCorrectly() {
        Specification<Car> spec = Specification
                .where(CarSpecification.hasBasePriceMin(new BigDecimal("20000.00")))
                .and(CarSpecification.hasBasePriceMax(new BigDecimal("50000.00")));
        Page<Car> result = carRepository.findAll(spec, PageRequest.of(0, 20));

        assertThat(result.getTotalElements()).isEqualTo(3);
    }

    @Test
    void givenCombinedSpecifications_whenFindAll_thenFiltersCorrectly() {
        Specification<Car> spec = Specification
                .where(CarSpecification.hasStatus(CarStatus.AVAILABLE))
                .and(CarSpecification.hasBrand("Honda"));
        Page<Car> result = carRepository.findAll(spec, PageRequest.of(0, 20));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getVin()).isEqualTo("1HGBH41JXMN109186");
    }

    @Test
    void givenNullSpecifications_whenFindAll_thenReturnsAll() {
        Specification<Car> spec = Specification
                .where(CarSpecification.hasStatus(null))
                .and(CarSpecification.hasBrand(null))
                .and(CarSpecification.hasBasePriceMin(null))
                .and(CarSpecification.hasBasePriceMax(null));
        Page<Car> result = carRepository.findAll(spec, PageRequest.of(0, 20));

        assertThat(result.getTotalElements()).isEqualTo(5);
    }

    @Test
    void givenPageRequest_whenFindAll_thenReturnsCorrectPage() {
        Page<Car> firstPage = carRepository.findAll(PageRequest.of(0, 2));
        Page<Car> secondPage = carRepository.findAll(PageRequest.of(1, 2));

        assertThat(firstPage.getContent()).hasSize(2);
        assertThat(firstPage.getTotalElements()).isEqualTo(5);
        assertThat(firstPage.getTotalPages()).isEqualTo(3);
        assertThat(secondPage.getContent()).hasSize(2);
    }

    @Test
    void givenNewCar_whenSave_thenPersistsSuccessfully() {
        Car car = Car.builder()
                .vin("1N4AL3AP7FC201234")
                .brand("Ford")
                .model("Mustang")
                .manufacturedYear(2024)
                .color("Yellow")
                .fuelType(FuelType.GASOLINE)
                .transmission("Manual")
                .basePrice(new BigDecimal("55000.00"))
                .status(CarStatus.AVAILABLE)
                .build();

        Car savedCar = carRepository.save(car);

        assertThat(savedCar.getId()).isNotNull();
        assertThat(carRepository.findById(savedCar.getId())).isPresent();
    }

    @Test
    void givenExistingCar_whenDelete_thenRemovesSuccessfully() {
        long countBefore = carRepository.count();
        Car car = carRepository.findById(1L).orElseThrow();

        carRepository.delete(car);

        assertThat(carRepository.count()).isEqualTo(countBefore - 1);
        assertThat(carRepository.findById(1L)).isEmpty();
    }
}
