package com.interview.controllers;

import com.interview.models.bank.dto.BankRequest;
import com.interview.models.bank.dto.BankResponse;
import com.interview.models.user.User;
import com.interview.services.AuthService;
import com.interview.services.BankService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/bank")
public class BankController {

    private final BankService bankService;
    private final AuthService authService;

    public BankController(BankService bankService, AuthService authService) {
        this.bankService = bankService;
        this.authService = authService;
    }

    @PostMapping
    public ResponseEntity<?> createBank(@RequestHeader(name = "Authorization") String authorization,
                                        @RequestBody BankRequest request) {
        Optional<User> userOpt = authService.authenticate(authorization);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            BankResponse created = bankService.createBank(userOpt.get(), request);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<List<BankResponse>> listBanks(@RequestHeader(name = "Authorization") String authorization) {
        Optional<User> userOpt = authService.authenticate(authorization);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<BankResponse> banks = bankService.listBanks(userOpt.get());
        return ResponseEntity.ok(banks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BankResponse> getBank(@RequestHeader(name = "Authorization") String authorization,
                                                @PathVariable Long id) {
        Optional<User> userOpt = authService.authenticate(authorization);
        return userOpt.map(user -> bankService.getBank(user, id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build())).orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAccount(@RequestHeader(name = "Authorization") String authorization,
                                           @PathVariable Long id,
                                           @RequestBody BankRequest request) {
        Optional<User> userOpt = authService.authenticate(authorization);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (request == null || request.getAccountNumber() == null) {
            return ResponseEntity.badRequest().body("accountNumber is required");
        }
        return bankService.updateAccountNumber(userOpt.get(), id, request.getAccountNumber())
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.<BankResponse>status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> softDelete(@RequestHeader(name = "Authorization") String authorization,
                                        @PathVariable Long id) {
        Optional<User> userOpt = authService.authenticate(authorization);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        boolean deleted = bankService.softDelete(userOpt.get(), id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
