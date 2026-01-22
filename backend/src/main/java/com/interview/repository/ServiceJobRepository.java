package com.interview.repository;

import com.interview.model.ServiceJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceJobRepository extends JpaRepository<ServiceJob, Long> {
}
