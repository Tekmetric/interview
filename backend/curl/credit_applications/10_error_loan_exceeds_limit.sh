#!/usr/bin/env bash
# POST /api/v1/credit-applications — 400: loan amount exceeds 5x annual income
# Jane's annualIncome = 95000 => max loan = 475000; requesting 500000 triggers @ValidLoanAmount
# Expected: 400 ProblemDetail, cross-field validation failure

JANE_CUSTOMER_ID="018fae10-0000-7000-8000-000000000001"

curl -s -u "api-user:changeme" -X POST "http://localhost:8080/api/v1/credit-applications" \
  -H "Content-Type: application/json" \
  -d "{
    \"customerId\": \"$JANE_CUSTOMER_ID\",
    \"requestedLoanAmount\": 500000.00,
    \"loanPurpose\": \"VEHICLE_PURCHASE\",
    \"monthlyDebt\": 0.00,
    \"documents\": [
      { \"documentType\": \"PROOF_OF_INCOME\" }
    ]
  }" | jq .
