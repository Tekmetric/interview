#!/usr/bin/env bash
# PATCH /api/v1/credit-applications/:id/status — 409: SUBMITTED cannot jump directly to APPROVED
# State machine: SUBMITTED -> UNDER_REVIEW only; skipping UNDER_REVIEW is invalid
# Expected: 409 ProblemDetail, InvalidApplicationStateException

JANE_APP_ID="018fae20-0000-7000-8000-000000000001"

curl -s -u "api-user:changeme" -X PATCH "http://localhost:8080/api/v1/credit-applications/$JANE_APP_ID/status" \
  -H "Content-Type: application/json" \
  -d '{"status": "APPROVED"}' | jq .
