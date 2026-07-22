package com.interview.repository;

import com.interview.entity.CarEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CarRepository extends CrudRepository<CarEntity, UUID> {

    List<CarEntity> findAll();

    CarEntity saveAndFlush(final CarEntity carEntity);
}
