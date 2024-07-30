package com.interview.autoshop.repositories;

import com.interview.autoshop.model.ServiceRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {

    List<ServiceRequest> findByCarOwnerEmailStartsIgnoreCaseWithAndStatusNotLike(final String email, final String status);
}
