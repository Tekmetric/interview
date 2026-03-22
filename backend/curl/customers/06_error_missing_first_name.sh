#!/usr/bin/env bash
# POST /api/v1/customers — 400: firstName is required
# Expected: 400 ProblemDetail, validation error on firstName

curl -s -u "api-user:changeme" -X POST "http://localhost:8080/api/v1/customers" \
  -H "Content-Type: application/json" \
  -d '{
    "lastName": "Ghost",
    "email": "ghost@example.com",
    "dateOfBirth": "1995-01-01",
    "ssn": "000-00-0001",
    "address": {
      "street": "1 Unknown St",
      "city": "Austin",
      "state": "TX",
      "zipCode": "78700"
    },
    "employmentDetails": {
      "employmentStatus": "EMPLOYED",
      "annualIncome": 50000.00
    }
  }' | jq .
