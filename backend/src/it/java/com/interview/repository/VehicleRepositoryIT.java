package com.interview.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.interview.domain.Vin;
import com.interview.repository.entity.VehicleEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;
import java.util.UUID;

@DataJpaTest
class VehicleRepositoryIT {

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
        assertThat(found).usingRecursiveComparison().isEqualTo(saved);
    }

    @Test
    void findByIdReturnsVehicle() {
        final VehicleEntity entity = entityWith(new Vin("1HGBH41JXMN109186"));
        entityManager.persistAndFlush(entity);
        entityManager.clear();

        final Optional<VehicleEntity> result = vehicleRepository.findById(entity.getId());

        assertThat(result).isPresent();
        assertThat(result.get()).usingRecursiveComparison().isEqualTo(entity);
    }

    @Test
    void findAllReturnsPage() {
        entityManager.persistAndFlush(entityWith(new Vin("1HGBH41JXMN109186")));
        entityManager.persistAndFlush(entityWith(new Vin("2HGBH41JXMN109186")));
        entityManager.clear();

        final Page<VehicleEntity> page = vehicleRepository.findAll(PageRequest.of(0, 10));

        assertThat(page.getContent()).hasSize(2);
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

    private static VehicleEntity entityWith(Vin vin) {
        final VehicleEntity entity = new VehicleEntity();
        entity.setVin(vin);
        return entity;
    }
}
