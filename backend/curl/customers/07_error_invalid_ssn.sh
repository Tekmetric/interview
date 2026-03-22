#!/usr/bin/env bash
# POST /api/v1/customers — 400: SSN must match ###-##-#### format
# Expected: 400 ProblemDetail, @ValidSSN constraint violation

curl -s -u "api-user:changeme" -X POST "http://localhost:8080/api/v1/customers" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Bad",
    "lastName": "SSN",
    "email": "bad.ssn@example.com",
    "dateOfBirth": "1990-01-01",
    "ssn": "NOT-AN-SSN",
    "address": {
      "street": "1 Test St",
      "city": "Austin",
      "state": "TX",
      "zipCode": "78700"
    },
    "employmentDetails": {
      "employmentStatus": "EMPLOYED",
      "annualIncome": 50000.00
    }
  }' | jq .
