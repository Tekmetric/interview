#!/usr/bin/env bash
# GET /api/v1/credit-applications/customers/:customerId — all applications for Jane Doe (paginated)
# Expected: 200, page containing 1 application with documentDownloadUrls[]

JANE_CUSTOMER_ID="018fae10-0000-7000-8000-000000000001"

curl -s -u "api-user:changeme" -X GET "http://localhost:8080/api/v1/credit-applications/customers/$JANE_CUSTOMER_ID" \
  -H "Accept: application/json" | jq .
