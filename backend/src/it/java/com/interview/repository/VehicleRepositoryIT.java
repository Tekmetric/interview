package com.interview.repository;

import static com.interview.assertion.QueryAssert.assertThatQuery;
import static com.interview.fixture.VehicleDataFixture.CUSTOMER_1_ID;
import static org.assertj.core.api.Assertions.assertThat;

import com.interview.domain.Vin;
import com.interview.repository.entity.CustomerEntity;
import com.interview.repository.entity.VehicleEntity;
import java.util.Optional;
import java.util.UUID;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@Sql("/datasets/vehicle-data.sql")
class VehicleRepositoryIT {

    @Autowired
    VehicleRepository vehicleRepository;

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    SessionFactory sessionFactory;

    private Statistics statistics;

    @BeforeEach
    void setUp() {
        statistics = sessionFactory.getStatistics();
    }

    @Test
    void savePersistsVehicle() {
        final VehicleEntity entity = entityWith(new Vin("1HGBH41JXMN109186"));

        statistics.clear();
        final VehicleEntity saved = vehicleRepository.save(entity);
        entityManager.flush();
        assertThatQuery(statistics).hasInsertCount(1).hasNoOtherOperations();

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

        statistics.clear();
        final Optional<VehicleEntity> result = vehicleRepository.findById(entity.getId());
        assertThatQuery(statistics).hasEntityLoadCount(1).hasNoOtherOperations();

        assertThat(result).get().usingRecursiveComparison()
                .ignoringFields("customer")
                .isEqualTo(entity);
        assertThat(result.get().getCustomer().getId()).isEqualTo(entity.getCustomer().getId());
    }

    @Test
    void findAllReturnsPage() {
        entityManager.persistAndFlush(entityWith(new Vin("1HGBH41JXMN109186")));
        entityManager.persistAndFlush(entityWith(new Vin("2HGBH41JXMN109186")));
        entityManager.clear();

        statistics.clear();
        final Page<VehicleEntity> page = vehicleRepository.findAll(PageRequest.of(0, 10));
        assertThatQuery(statistics).hasQueryCount(1).hasEntityLoadCount(7).hasNoOtherOperations();

        assertThat(page.getContent()).hasSize(7);
    }

    @Test
    void deleteRemovesVehicle() {
        final VehicleEntity entity = entityWith(new Vin("1HGBH41JXMN109186"));
        entityManager.persistAndFlush(entity);
        final UUID id = entity.getId();

        statistics.clear();
        vehicleRepository.delete(entity);
        entityManager.flush();
        assertThatQuery(statistics).hasDeleteCount(1).hasNoOtherOperations();

        entityManager.clear();

        assertThat(vehicleRepository.findById(id)).isEmpty();
    }

    private VehicleEntity entityWith(Vin vin) {
        final VehicleEntity entity = new VehicleEntity();
        entity.setVin(vin);
        entity.setCustomer(entityManager.find(CustomerEntity.class, UUID.fromString(CUSTOMER_1_ID)));
        return entity;
    }
}
