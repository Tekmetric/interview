#!/usr/bin/env bash
# DELETE /api/v1/credit-applications/:id — delete Jane's application
# Expected: 204 No Content

JANE_APP_ID="018fae20-0000-7000-8000-000000000001"

curl -s -u "api-user:changeme" -o /dev/null -w "HTTP %{http_code}\n" \
  -X DELETE "http://localhost:8080/api/v1/credit-applications/$JANE_APP_ID"
