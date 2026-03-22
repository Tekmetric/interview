#!/usr/bin/env bash
# POST /api/v1/customers — create a new customer
# Expected: 201 Created, Location header, SSN masked in response body

curl -s -u "api-user:changeme" -X POST "http://localhost:8080/api/v1/customers" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Carlos",
    "lastName": "Rivera",
    "email": "carlos.rivera@example.com",
    "phone": "+15555550199",
    "dateOfBirth": "1990-06-15",
    "ssn": "321-54-9870",
    "address": {
      "street": "789 Pine Rd",
      "city": "San Antonio",
      "state": "TX",
      "zipCode": "78201"
    },
    "employmentDetails": {
      "employmentStatus": "EMPLOYED",
      "employerName": "Rivera Auto Group",
      "annualIncome": 85000.00
    }
  }' | jq .
