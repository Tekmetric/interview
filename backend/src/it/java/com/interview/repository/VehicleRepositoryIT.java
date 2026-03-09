package com.interview.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.interview.domain.Vin;
import com.interview.repository.entity.CustomerEntity;
import com.interview.repository.entity.VehicleEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;
import java.util.UUID;

@DataJpaTest
@Sql("/datasets/vehicle-data.sql")
class VehicleRepositoryIT {

    private static final UUID CUSTOMER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    @Autowired
    VehicleRepository vehicleRepository;

    @Autowired
    TestEntityManager entityManager;

    @Test
    void savePersistsVehicle() {
        final VehicleEntity entity = entityWith(new Vin("1HGBH41JXMN109186"));

        final VehicleEntity saved = vehicleRepository.save(entity);
        entityManager.flush();
        entityManager.clear();

        final VehicleEntity found = entityManager.find(VehicleEntity.class, saved.getId());
        assertThat(found).usingRecursiveComparison()
                .ignoringFields("customer")
                .isEqualTo(saved);
        assertThat(found.getCustomer().getId()).isEqualTo(saved.getCustomer().getId());
    }

    @Test
    void findByIdReturnsVehicle() {
        final VehicleEntity entity = entityWith(new Vin("1HGBH41JXMN109186"));
        entityManager.persistAndFlush(entity);
        entityManager.clear();

        final Optional<VehicleEntity> result = vehicleRepository.findById(entity.getId());

        assertThat(result).isPresent();
        assertThat(result.get()).usingRecursiveComparison()
                .ignoringFields("customer")
                .isEqualTo(entity);
        assertThat(result.get().getCustomer().getId()).isEqualTo(entity.getCustomer().getId());
    }

    @Test
    void findAllReturnsPage() {
        entityManager.persistAndFlush(entityWith(new Vin("1HGBH41JXMN109186")));
        entityManager.persistAndFlush(entityWith(new Vin("2HGBH41JXMN109186")));
        entityManager.clear();

        final Page<VehicleEntity> page = vehicleRepository.findAll(PageRequest.of(0, 10));

        assertThat(page.getContent()).hasSize(3);
    }

    @Test
    void deleteRemovesVehicle() {
        final VehicleEntity entity = entityWith(new Vin("1HGBH41JXMN109186"));
        entityManager.persistAndFlush(entity);
        final UUID id = entity.getId();

        vehicleRepository.delete(entity);
        entityManager.flush();
        entityManager.clear();

        assertThat(vehicleRepository.findById(id)).isEmpty();
    }

    private VehicleEntity entityWith(Vin vin) {
        final VehicleEntity entity = new VehicleEntity();
        entity.setVin(vin);
        entity.setCustomer(entityManager.find(CustomerEntity.class, CUSTOMER_ID));
        return entity;
    }
}
