package com.interview.transactions.api;

import com.interview.transactions.api.model.CreateTransactionRequest;
import com.interview.transactions.api.model.UpdateTransactionRequest;
import com.interview.transactions.service.TransactionService;
import com.interview.transactions.service.dto.Transaction;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/transactions", produces = "application/json")
@Validated
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "500", description = "Internal error"),
            @ApiResponse(responseCode = "400", description = "Invalid data request"),
            @ApiResponse(responseCode = "201", description = "Transaction created")
    })
    @PostMapping(consumes = "application/json")
    public ResponseEntity<Transaction> createTransaction(
            @Valid @RequestBody CreateTransactionRequest request
    ) {
        Transaction result = transactionService.createTransaction(
                Transaction.builder().amount(request.getAmount())
                        .currency(request.getCurrency())
                        .build()
        );
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "500", description = "Internal error"),
            @ApiResponse(responseCode = "200", description = "Transactions retreived sucessfully")
    })
    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        List<Transaction> transactions = transactionService.getAllTransactions();
        return ResponseEntity.ok(transactions);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "500", description = "Internal error"),
            @ApiResponse(responseCode = "404", description = "Transaction not found"),
            @ApiResponse(responseCode = "200", description = "Transaction retreived sucessfully")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(
            @NotNull @PathVariable Long id
    ) {
        Transaction transaction = transactionService.getTransactionById(id);
        return ResponseEntity.ok(transaction);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "500", description = "Internal error"),
            @ApiResponse(responseCode = "404", description = "Transaction not found"),
            @ApiResponse(responseCode = "200", description = "Transaction updated sucessfully")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Transaction> update(@PathVariable Long id, @Valid @RequestBody UpdateTransactionRequest request) {
        return ResponseEntity.ok(transactionService.update(Transaction.builder()
                        .id(id)
                        .amount(request.getAmount())
                        .status(request.getStatus())
                        .currency(request.getCurrency())
                        .build()));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "500", description = "Internal error"),
            @ApiResponse(responseCode = "404", description = "Transaction not found"),
            @ApiResponse(responseCode = "204", description = "Transaction deleted sucessfully")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        transactionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
