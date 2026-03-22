#!/usr/bin/env bash
# PATCH /api/v1/credit-applications/:id/status — approve John's UNDER_REVIEW application
# Expected: 200, status=APPROVED, decidedAt populated

JOHN_APP_ID="018fae20-0000-7000-8000-000000000002"

curl -s -u "api-user:changeme" -X PATCH "http://localhost:8080/api/v1/credit-applications/$JOHN_APP_ID/status" \
  -H "Content-Type: application/json" \
  -d '{"status": "APPROVED"}' | jq .
