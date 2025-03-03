package com.interview.repository;

import com.interview.model.Animal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
interface AnimalJpaRepositoryBase extends JpaRepository<Animal, Long>, JpaSpecificationExecutor<Animal> {}