package com.interview.repository;

import com.interview.config.JpaConfig;
import com.interview.entity.Vehicle;
import jakarta.persistence.criteria.Predicate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import(JpaConfig.class)
@TestPropertySource(properties = "spring.sql.init.mode=never")
class VehicleRepositoryTest {

    @Autowired
    private VehicleRepository repository;

    @Test
    void uniqueVinIsEnforced() {
        repository.saveAndFlush(new Vehicle("1HGCM82633A004352", "Honda", "Accord", 2021, "ABC1234", 42000));
        Vehicle duplicate = new Vehicle("1HGCM82633A004352", "Honda", "Civic", 2019, "XYZ9999", 60000);
        assertThatThrownBy(() -> repository.saveAndFlush(duplicate))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void filterByMakeLowerMatchesDespiteStoredCase() {
        repository.saveAndFlush(new Vehicle("1HGCM82633A004352", "Honda", "Accord", 2021, "ABC1234", 42000));
        repository.saveAndFlush(new Vehicle("5YJSA1E26HF000337", "Tesla", "Model S", 2017, "EV22222", 88000));

        Specification<Vehicle> spec = (root, query, cb) -> cb.equal(root.get("makeLower"), "tesla");
        assertThat(repository.findAll(spec))
                .extracting(Vehicle::getMake)
                .containsExactly("Tesla");
    }

    @Test
    void filterByMakeModelAndYearCombined() {
        repository.saveAndFlush(new Vehicle("1HGCM82633A004352", "Honda", "Accord", 2021, "ABC1234", 42000));
        repository.saveAndFlush(new Vehicle("JH4KA7650MC012345", "Honda", "Accord", 1991, "OLD0001", 210000));
        repository.saveAndFlush(new Vehicle("5YJSA1E26HF000337", "Honda", "Civic", 2021, "CV11111", 30000));

        Specification<Vehicle> spec = (root, query, cb) -> {
            Predicate make = cb.equal(root.get("makeLower"), "honda");
            Predicate model = cb.equal(root.get("modelLower"), "accord");
            Predicate year = cb.equal(root.get("year"), 2021);
            return cb.and(make, model, year);
        };
        assertThat(repository.findAll(spec))
                .hasSize(1)
                .first()
                .extracting(Vehicle::getVin)
                .isEqualTo("1HGCM82633A004352");
    }
}
