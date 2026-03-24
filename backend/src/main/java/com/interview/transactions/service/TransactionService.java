package com.interview.transactions.service;

import com.interview.transactions.common.exceptions.TransactionNotFound;
import com.interview.transactions.repo.TransactionRepo;
import com.interview.transactions.repo.entities.TransactionEntity;
import com.interview.transactions.service.dto.Currency;
import com.interview.transactions.service.dto.Status;
import com.interview.transactions.service.dto.Transaction;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    private final TransactionRepo repo;

    public TransactionService(TransactionRepo repo) {
        this.repo = repo;
    }

    @Transactional
    public Transaction getTransactionById(Long transactionId) {
        Optional<TransactionEntity> tEntity = repo.findById(transactionId);

        return entityToDto(tEntity.orElseThrow(TransactionNotFound::new));
    }

    @Transactional
    public List<Transaction> getAllTransactions() {
        return ((List<TransactionEntity>)repo.findAll()).stream().map(this::entityToDto).toList();
    }

    @Transactional
    public Transaction createTransaction(Transaction transaction) {
        TransactionEntity saved = repo.save(TransactionEntity.builder()
                .amount(transaction.amount()).status(Status.PLACED.name())
                .currency(transaction.currency().name())
                .build());

        return entityToDto(saved);

    }

    @Transactional
    public void delete(Long id) {
        if(!repo.existsById(id)) {
            throw new TransactionNotFound();
        }

        repo.deleteById(id);
    }

    @Transactional
    public Transaction update(Transaction transaction) {
        Optional<TransactionEntity> entity = repo.findById(transaction.id());

        TransactionEntity te = entity.orElseThrow(TransactionNotFound::new);

        te.setStatus(transaction.status().name());
        te.setAmount(transaction.amount());
        te.setCurrency(transaction.currency().name());

        return entityToDto(repo.save(te));

    }

    Transaction entityToDto(final TransactionEntity entity) {
        return Transaction.builder().id(entity.getId())
                .amount(entity.getAmount())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .currency(Currency.valueOf(entity.getCurrency()))
                .status(Status.valueOf(entity.getStatus()))
                .build();
    }
}
