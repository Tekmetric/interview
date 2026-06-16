#!/usr/bin/env bash
# POST /api/v1/credit-applications — 404: customer does not exist
# Expected: 404 ProblemDetail, CustomerNotFoundException

curl -s -u "api-user:changeme" -X POST "http://localhost:8080/api/v1/credit-applications" \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "00000000-0000-0000-0000-000000000000",
    "requestedLoanAmount": 20000.00,
    "loanPurpose": "VEHICLE_PURCHASE",
    "monthlyDebt": 300.00,
    "documents": [
      { "documentType": "PROOF_OF_INCOME" }
    ]
  }' | jq .
