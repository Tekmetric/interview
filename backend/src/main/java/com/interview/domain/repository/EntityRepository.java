package com.interview.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Optional;

/**
 * General repository structure inherited by all repository
 */
@NoRepositoryBean
@Transactional(propagation = Propagation.MANDATORY)
public interface EntityRepository<ENTITY, ID extends Serializable> extends JpaRepository<ENTITY, ID>, JpaSpecificationExecutor<ENTITY> {
    Optional<ENTITY> findOneById(ID id);
}
