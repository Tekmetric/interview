package com.interview.domain.repository;

import com.interview.domain.model.User;

import java.util.Optional;

public interface UserRepository extends EntityRepository<User, Long> {
    Optional<User> findOneByEmail(String email);
}
