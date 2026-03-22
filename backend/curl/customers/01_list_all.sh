#!/usr/bin/env bash
# GET /api/v1/customers — paginated list, newest first
# Expected: 200, page of 3 seeded customers

curl -s -u "api-user:changeme" -X GET "http://localhost:8080/api/v1/customers?page=0&size=10&sort=dateCreated,desc" \
  -H "Accept: application/json" | jq .
