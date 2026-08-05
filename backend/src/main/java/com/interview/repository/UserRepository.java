package com.interview.repository;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.interview.model.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.organization = :org AND u.administrator = TRUE")
    Optional<User> findOrganizationAdministrator(@Param("org") String organization);

    @Transactional
    long deleteByNameAndOrganization(String name, String organization);

    boolean existsByNameAndOrganization(String name, String organization);
    
    boolean existsByOrganization(String organization);

    int countByOrganization(String organization);

    List<User> findByNameAndOrganization(String name, String organization);

    List<User> findByOrganization(String organization);

    List<User> findByName(String name);
}
