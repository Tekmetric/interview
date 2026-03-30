package com.interview.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.interview.persistence.entity.CreditApplication;

@Repository
public interface CreditApplicationRepository
        extends JpaRepository<CreditApplication, UUID>,
                JpaSpecificationExecutor<CreditApplication> {

    Page<CreditApplication> findByCustomerId(final UUID customerId, final Pageable pageable);

    @Query("SELECT DISTINCT ca FROM CreditApplication ca LEFT JOIN FETCH ca.documents WHERE ca.customer.id = :customerId")
    List<CreditApplication> findByCustomerIdWithDocuments(@Param("customerId") UUID customerId);
}
