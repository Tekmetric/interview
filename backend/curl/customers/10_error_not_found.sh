#!/usr/bin/env bash
# GET /api/v1/customers/:id — 404: customer does not exist
# Expected: 404 ProblemDetail, CustomerNotFoundException

curl -s -u "api-user:changeme" -X GET "http://localhost:8080/api/v1/customers/00000000-0000-0000-0000-000000000000" \
  -H "Accept: application/json" | jq .
