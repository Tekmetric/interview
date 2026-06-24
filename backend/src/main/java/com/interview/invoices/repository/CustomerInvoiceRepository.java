package com.interview.invoices.repository;

import com.interview.invoices.domain.CustomerInvoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerInvoiceRepository extends JpaRepository<CustomerInvoice, String>, JpaSpecificationExecutor<CustomerInvoice> {
}
