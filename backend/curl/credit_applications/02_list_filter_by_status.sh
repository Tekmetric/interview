#!/usr/bin/env bash
# GET /api/v1/credit-applications?status=SUBMITTED — filter by status via JPA Specification
# Expected: 200, 1 result — Jane Doe's application

curl -s -u "api-user:changeme" -X GET "http://localhost:8080/api/v1/credit-applications?status=SUBMITTED" \
  -H "Accept: application/json" | jq .
