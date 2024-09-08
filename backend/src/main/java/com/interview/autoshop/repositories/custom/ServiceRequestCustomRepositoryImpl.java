package com.interview.autoshop.repositories.custom;

import com.interview.autoshop.model.ServiceRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaContext;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class ServiceRequestCustomRepositoryImpl implements ServiceRequestCustomRepository {

    private final EntityManager em;

    private CriteriaBuilder cb;

    @Autowired
    public ServiceRequestCustomRepositoryImpl(JpaContext context) {
        this.em = context.getEntityManagerByManagedType(ServiceRequest.class);
        this.cb = em.getCriteriaBuilder();
    }

    @Override
    public List<ServiceRequest> findServiceRequestByParams(Boolean isOpen) {
        CriteriaQuery<ServiceRequest> query = cb.createQuery(ServiceRequest.class);
        Root<ServiceRequest> serviceRequestRoot = query.from(ServiceRequest.class);

        List<Predicate> predicates = new ArrayList<>();
        if(isOpen != null && isOpen){
            predicates.add(cb.notEqual(serviceRequestRoot.get("status"), "completed"));
        }

        return em.createQuery(query).getResultList();
    }
}
