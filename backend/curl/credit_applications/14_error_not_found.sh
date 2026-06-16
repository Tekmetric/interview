#!/usr/bin/env bash
# GET /api/v1/credit-applications/:id — 404: application does not exist
# Expected: 404 ProblemDetail, CreditApplicationNotFoundException

curl -s -u "api-user:changeme" -X GET "http://localhost:8080/api/v1/credit-applications/00000000-0000-0000-0000-000000000000" \
  -H "Accept: application/json" | jq .
