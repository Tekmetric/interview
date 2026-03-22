#!/usr/bin/env bash
# PUT /api/v1/customers/:id — update John Smith's employer name and annual income
# UpdateCustomerRequest accepts: firstName, lastName, email, phone, address, employmentDetails.
# dateOfBirth and ssn are immutable after creation and are not accepted fields.
# Only fields included in the body are applied (null-safe partial update via MapStruct).
# Expected: 200, annualIncome updated to 135000.00

JOHN_ID="018fae10-0000-7000-8000-000000000002"

curl -s -u "api-user:changeme" -X PUT "http://localhost:8080/api/v1/customers/$JOHN_ID" \
  -H "Content-Type: application/json" \
  -d '{
    "employmentDetails": {
      "employmentStatus": "SELF_EMPLOYED",
      "employerName": "Smith Consulting LLC",
      "annualIncome": 135000.00
    }
  }' | jq .
