package com.interview.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.interview.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

   
}
