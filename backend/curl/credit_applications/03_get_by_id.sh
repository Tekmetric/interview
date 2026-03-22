#!/usr/bin/env bash
# GET /api/v1/credit-applications/:id — fetch Jane Doe's seeded SUBMITTED application
# Expected: 200, status=SUBMITTED, customerId and customerName populated

JANE_APP_ID="018fae20-0000-7000-8000-000000000001"

curl -s -u "api-user:changeme" -X GET "http://localhost:8080/api/v1/credit-applications/$JANE_APP_ID" \
  -H "Accept: application/json" | jq .
