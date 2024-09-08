package com.interview.autoshop.repositories.custom;

import com.interview.autoshop.model.ServiceRequest;

import java.util.List;

/**
 * Custom Repository example to handle any complex queries over the data in future
 */
public interface ServiceRequestCustomRepository {

    List<ServiceRequest> findServiceRequestByParams(Boolean isOpen);
}
