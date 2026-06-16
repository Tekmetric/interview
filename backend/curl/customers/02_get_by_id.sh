#!/usr/bin/env bash
# GET /api/v1/customers/:id — fetch seeded customer Jane Doe
# Expected: 200, SSN masked as ***-**-6789

JANE_ID="018fae10-0000-7000-8000-000000000001"

curl -s -u "api-user:changeme" -X GET "http://localhost:8080/api/v1/customers/$JANE_ID" \
  -H "Accept: application/json" | jq .
