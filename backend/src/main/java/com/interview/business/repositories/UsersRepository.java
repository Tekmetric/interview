package com.interview.business.repositories;

import com.interview.business.domain.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository  extends JpaRepository<AppUser, String>, JpaSpecificationExecutor<AppUser> {

    boolean existsByEmail(String email);

    Optional<AppUser> findOneByEmail(String email);

}
