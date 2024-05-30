package com.interview.db;

import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Integer> {

  User findByUsername(String username);
}