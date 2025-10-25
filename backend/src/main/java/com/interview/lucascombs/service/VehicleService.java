package com.interview.lucascombs.service;

import com.interview.lucascombs.dao.VehicleDao;
import com.interview.lucascombs.entity.Vehicle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VehicleService {

    private final VehicleDao vehicleDao;

    @Autowired
    public VehicleService(VehicleDao vehicleDao) {
        this.vehicleDao = vehicleDao;
    }

    public Optional<Vehicle> getById(Long id) {
        return Optional.ofNullable(vehicleDao.getOne(id));
    }

    public Page<Vehicle> getAll(Integer page, Integer size) {
        return vehicleDao.findAll(PageRequest.of(
                Optional.ofNullable(page).orElse(0),
                Optional.ofNullable(size).orElse(10)));
    }

    public void deleteById(Long id) {
        vehicleDao.deleteById(id);
    }

    public Vehicle save(Vehicle entity) {
        return vehicleDao.saveAndFlush(entity);
    }
}
