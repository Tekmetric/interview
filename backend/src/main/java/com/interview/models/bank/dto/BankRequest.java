package com.interview.models.bank.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankRequest {
    private String accountNumber;
    private String routingNumber;
}