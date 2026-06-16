#!/usr/bin/env bash
# POST /api/v1/credit-applications — 400: customerId is required
# Expected: 400 ProblemDetail, validation error on customerId

curl -s -u "api-user:changeme" -X POST "http://localhost:8080/api/v1/credit-applications" \
  -H "Content-Type: application/json" \
  -d '{
    "requestedLoanAmount": 20000.00,
    "loanPurpose": "VEHICLE_PURCHASE",
    "monthlyDebt": 300.00,
    "documents": [
      { "documentType": "PROOF_OF_INCOME" }
    ]
  }' | jq .
