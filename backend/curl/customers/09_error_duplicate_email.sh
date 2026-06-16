#!/usr/bin/env bash
# POST /api/v1/customers — 409: email already in use (jane.doe@example.com is seeded)
# Expected: 409 ProblemDetail, unique constraint violation on email

curl -s -u "api-user:changeme" -X POST "http://localhost:8080/api/v1/customers" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Duplicate",
    "lastName": "Doe",
    "email": "jane.doe@example.com",
    "dateOfBirth": "1985-03-15",
    "ssn": "999-88-7766",
    "address": {
      "street": "999 Dupe St",
      "city": "Austin",
      "state": "TX",
      "zipCode": "78701"
    },
    "employmentDetails": {
      "employmentStatus": "EMPLOYED",
      "annualIncome": 60000.00
    }
  }' | jq .
