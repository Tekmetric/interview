#!/usr/bin/env bash
# PATCH /api/v1/credit-applications/:id/status — advance Jane's app SUBMITTED -> UNDER_REVIEW
# Expected: 200, status=UNDER_REVIEW
# Side effect: SqsPublisher fires (no-op locally since aws.enabled=false by default)

JANE_APP_ID="018fae20-0000-7000-8000-000000000001"

curl -s -u "api-user:changeme" -X PATCH "http://localhost:8080/api/v1/credit-applications/$JANE_APP_ID/status" \
  -H "Content-Type: application/json" \
  -d '{"status": "UNDER_REVIEW"}' | jq .
