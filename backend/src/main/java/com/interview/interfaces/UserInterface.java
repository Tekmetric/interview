package com.interview.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;

import com.interview.model.UserModel;

public interface UserInterface extends JpaRepository<UserModel, Long>{
    UserModel findByUsername(String username);
    UserModel findByEmail(String email);
    UserModel findById(long id);
}
