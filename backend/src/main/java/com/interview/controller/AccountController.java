package com.interview.controller;

import com.interview.dto.AccountMapper;
import com.interview.dto.AccountRequest;
import com.interview.dto.AccountResponse;
import com.interview.model.Account;
import com.interview.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Tag(name = "Accounts", description = "Account management APIs")
@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;
    private final AccountMapper accountMapper;

    @Autowired
    public AccountController(AccountService accountService, AccountMapper accountMapper) {
        this.accountService = accountService;
        this.accountMapper = accountMapper;
    }

    @Operation(summary = "Get all accounts with pagination",
               description = "Retrieve a paginated list of all accounts. Use query parameters: page (0-indexed), size, sort (e.g., 'firstName,asc')")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved paginated list of accounts")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<AccountResponse>> getAllAccounts(
            @Parameter(description = "Pagination parameters (page, size, sort)") Pageable pageable) {
        Page<Account> accounts = accountService.getAllAccounts(pageable);
        Page<AccountResponse> responses = accounts.map(accountMapper::toResponse);
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Get account by ID", description = "Retrieve a specific account by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account found"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccountById(
            @Parameter(description = "ID of the account to retrieve") @PathVariable UUID id) {
        return accountService.getAccountById(id)
                .map(accountMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get account by email", description = "Retrieve a specific account by their email address")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account found"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @GetMapping("/email/{email}")
    public ResponseEntity<AccountResponse> getAccountByEmail(
            @Parameter(description = "Email of the account to retrieve") @PathVariable String email) {
        return accountService.getAccountByEmail(email)
                .map(accountMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create new account", description = "Create a new account account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Account created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or email already exists")
    })
    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Account request object")
            @Valid @RequestBody AccountRequest request) {
        try {
            Account account = accountMapper.toEntity(request);
            Account createdAccount = accountService.createAccount(account);
            return ResponseEntity.status(HttpStatus.CREATED).body(accountMapper.toResponse(createdAccount));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @Operation(summary = "Update account", description = "Update an existing account by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account updated successfully"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input or email already taken")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<AccountResponse> updateAccount(
            @Parameter(description = "ID of the account to update") @PathVariable UUID id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Updated account request object")
            @Valid @RequestBody AccountRequest request) {
        try {
            Account accountDetails = accountMapper.toEntity(request);
            Account updatedAccount = accountService.updateAccount(id, accountDetails);
            return ResponseEntity.ok(accountMapper.toResponse(updatedAccount));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @Operation(summary = "Delete account", description = "Delete a account by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Account deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(
            @Parameter(description = "ID of the account to delete") @PathVariable UUID id) {
        try {
            accountService.deleteAccount(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Search accounts by name", description = "Search for accounts by first or last name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of accounts")
    })
    @GetMapping("/search")
    public ResponseEntity<List<AccountResponse>> searchAccountsByName(@RequestParam String name) {
        List<Account> accounts = accountService.searchAccountsByName(name);
        List<AccountResponse> responses = accounts.stream()
                .map(accountMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
}