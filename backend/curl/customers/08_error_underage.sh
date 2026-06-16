#!/usr/bin/env bash
# POST /api/v1/customers — 400: customer must be at least 18 years old
# Expected: 400 ProblemDetail, @ValidAdultAge constraint violation

curl -s -u "api-user:changeme" -X POST "http://localhost:8080/api/v1/customers" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Minor",
    "lastName": "Applicant",
    "email": "minor@example.com",
    "dateOfBirth": "2015-01-01",
    "ssn": "111-22-3344",
    "address": {
      "street": "1 Kid St",
      "city": "Austin",
      "state": "TX",
      "zipCode": "78700"
    },
    "employmentDetails": {
      "employmentStatus": "EMPLOYED",
      "annualIncome": 10000.00
    }
  }' | jq .
