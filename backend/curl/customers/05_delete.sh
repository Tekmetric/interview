#!/usr/bin/env bash
# DELETE /api/v1/customers/:id — delete John Smith
# Expected: 204 No Content

JOHN_ID="018fae10-0000-7000-8000-000000000002"

curl -s -u "api-user:changeme" -o /dev/null -w "HTTP %{http_code}\n" \
  -X DELETE "http://localhost:8080/api/v1/customers/$JOHN_ID"
