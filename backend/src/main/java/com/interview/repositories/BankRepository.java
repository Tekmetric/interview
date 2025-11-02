package com.interview.repositories;

import com.interview.models.bank.Bank;
import com.interview.models.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BankRepository extends JpaRepository<Bank, Long> {
    List<Bank> findByUserAndDeletedFalse(User user);
    Optional<Bank> findByIdAndUserAndDeletedFalse(Long id, User user);
}