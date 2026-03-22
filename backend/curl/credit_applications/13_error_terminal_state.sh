#!/usr/bin/env bash
# PATCH /api/v1/credit-applications/:id/status — 409: APPROVED is a terminal state
# Maria's application is seeded as APPROVED; no further transitions are allowed
# Expected: 409 ProblemDetail, InvalidApplicationStateException

MARIA_APP_ID="018fae20-0000-7000-8000-000000000003"

curl -s -u "api-user:changeme" -X PATCH "http://localhost:8080/api/v1/credit-applications/$MARIA_APP_ID/status" \
  -H "Content-Type: application/json" \
  -d '{"status": "DENIED"}' | jq .
