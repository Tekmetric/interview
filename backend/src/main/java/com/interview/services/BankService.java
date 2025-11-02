package com.interview.services;

import com.interview.models.bank.Bank;
import com.interview.models.bank.dto.BankRequest;
import com.interview.models.bank.dto.BankResponse;
import com.interview.models.user.User;
import com.interview.repositories.BankRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BankService {

    private final BankRepository bankRepository;

    public BankService(BankRepository bankRepository) {
        this.bankRepository = bankRepository;
    }

    public BankResponse createBank(User user, BankRequest request) {
        Bank bank = new Bank();
        bank.setAccountNumber(request.getAccountNumber());
        bank.setRoutingNumber(request.getRoutingNumber());
        bank.setUser(user);
        bank.setDeleted(false);
        Bank saved = bankRepository.save(bank);
        return toResponse(saved);
    }

    public List<BankResponse> listBanks(User user) {
        return bankRepository.findByUserAndDeletedFalse(user)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public Optional<BankResponse> getBank(User user, Long id) {
        return bankRepository.findByIdAndUserAndDeletedFalse(id, user)
                .map(this::toResponse);
    }

    public Optional<BankResponse> updateAccountNumber(User user, Long id, String newAccountNumber) {
        return bankRepository.findByIdAndUserAndDeletedFalse(id, user)
                .map(bank -> {
                    bank.setAccountNumber(newAccountNumber);
                    Bank saved = bankRepository.save(bank);
                    return toResponse(saved);
                });
    }

    public boolean softDelete(User user, Long id) {
        Optional<Bank> bankOpt = bankRepository.findByIdAndUserAndDeletedFalse(id, user);
        if (bankOpt.isPresent()) {
            Bank bank = bankOpt.get();
            bank.setDeleted(true);
            bankRepository.save(bank);
            return true;
        }
        return false;
    }

    private BankResponse toResponse(Bank bank) {
        BankResponse resp = new BankResponse();
        resp.setId(bank.getId());
        resp.setAccountNumber(maskAccountNumber(bank.getAccountNumber()));
        resp.setRoutingNumber(bank.getRoutingNumber());
        resp.setCreatedAt(bank.getCreatedAt());
        resp.setUpdatedAt(bank.getUpdatedAt());
        return resp;
    }

    private String maskAccountNumber(String accountNumber) {
        if (accountNumber == null) return null;
        int len = accountNumber.length();
        if (len <= 4) return accountNumber;
        String last4 = accountNumber.substring(len - 4);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len - 4; i++) sb.append('x');
        sb.append(last4);
        return sb.toString();
    }
}