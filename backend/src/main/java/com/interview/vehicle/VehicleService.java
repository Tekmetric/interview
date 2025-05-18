package com.interview.vehicle;

import com.interview.vehicle.exception.VehicleNotFoundException;
import com.interview.vehicle.model.Vehicle;
import com.interview.vehicle.model.VehicleCreate;
import com.interview.vehicle.model.VehicleId;
import com.interview.vehicle.model.VehicleUpdate;
import com.interview.vehicle.persistence.entity.VehicleEntity;
import com.interview.vehicle.persistence.respository.VehicleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository repository;

    public Vehicle get(VehicleId id) {
        return repository.findById(id)
                .orElseThrow(() -> new VehicleNotFoundException(id));
    }

    public Page<Vehicle> getAll(Pageable pageable) {
        return repository.findAll(pageable)
                .map(Vehicle.class::cast);
    }

    public Vehicle create(VehicleCreate create) {
        VehicleEntity vehicle = VehicleEntity.from(create);

        return repository.save(vehicle);
    }

    @Transactional
    public Vehicle update(VehicleId id, VehicleUpdate update) {
        Vehicle vehicle = get(id);

        vehicle.applyUpdate(update);

        return repository.save(((VehicleEntity) vehicle));
    }

    @Transactional
    public void delete(VehicleId id) {
        repository.deleteById(id);
    }
}
