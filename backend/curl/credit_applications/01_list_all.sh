#!/usr/bin/env bash
# GET /api/v1/credit-applications — paginated list, most recent first
# Expected: 200, page of 3 seeded applications across all statuses

curl -s -u "api-user:changeme" -X GET "http://localhost:8080/api/v1/credit-applications?page=0&size=10&sort=submittedAt,desc" \
  -H "Accept: application/json" | jq .
