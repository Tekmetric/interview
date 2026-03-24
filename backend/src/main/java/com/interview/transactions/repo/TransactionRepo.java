package com.interview.transactions.repo;

import com.interview.transactions.repo.entities.TransactionEntity;
import org.springframework.data.repository.CrudRepository;

public interface TransactionRepo extends CrudRepository<TransactionEntity, Long> {
}
