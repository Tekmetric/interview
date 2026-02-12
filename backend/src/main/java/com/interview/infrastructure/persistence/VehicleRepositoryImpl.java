package com.interview.infrastructure.persistence;

import com.interview.application.VehicleRepository;
import com.interview.domain.Vehicle;
import com.interview.infrastructure.jpa.VehicleEntity;
import com.interview.infrastructure.jpa.VehicleJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class VehicleRepositoryImpl implements VehicleRepository {

    private final VehicleJpaRepository jpaRepository;

    public VehicleRepositoryImpl(VehicleJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Vehicle save(Vehicle vehicle) {
        VehicleEntity entity = toEntity(vehicle);
        VehicleEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Vehicle> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Vehicle> findAll() {
        return jpaRepository.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public boolean existsByCustomerId(UUID customerId) {
        return jpaRepository.existsByCustomerId(customerId);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    private VehicleEntity toEntity(Vehicle domain) {
        return new VehicleEntity(
                domain.getId(),
                domain.getPlateNumber(),
                domain.getModel(),
                domain.getCustomerId()
        );
    }

    private Vehicle toDomain(VehicleEntity entity) {
        return new Vehicle(
                entity.getId(),
                entity.getPlateNumber(),
                entity.getModel(),
                entity.getCustomerId()
        );
    }
}
