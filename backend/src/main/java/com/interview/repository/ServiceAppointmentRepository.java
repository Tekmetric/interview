package com.interview.repository;

import com.interview.entity.ServiceAppointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceAppointmentRepository extends JpaRepository<ServiceAppointment, Long> {
    boolean existsByCustomerId(Long customerId);
}
