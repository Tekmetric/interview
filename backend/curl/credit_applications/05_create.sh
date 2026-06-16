#!/usr/bin/env bash
# POST /api/v1/credit-applications — submit a new application for Maria Garcia
# Maria's annualIncome = 72000; max loan = 360000; requesting 25000 — within limit
# Expected: 201 Created, status=SUBMITTED, submittedAt populated
# Response includes documentUploadUrls — one presigned S3 PUT URL per document (valid 15 min).
# Use 05b_upload_document.sh to PUT each supporting document to its presigned URL.

MARIA_CUSTOMER_ID="018fae10-0000-7000-8000-000000000003"

curl -s -u "api-user:changeme" -X POST "http://localhost:8080/api/v1/credit-applications" \
  -H "Content-Type: application/json" \
  -d "{
    \"customerId\": \"$MARIA_CUSTOMER_ID\",
    \"requestedLoanAmount\": 25000.00,
    \"loanPurpose\": \"VEHICLE_PURCHASE\",
    \"monthlyDebt\": 200.00,
    \"notes\": \"Looking for a reliable commuter vehicle\",
    \"documents\": [
      { \"documentType\": \"PROOF_OF_INCOME\", \"fileName\": \"paystub_jan_2024.pdf\" },
      { \"documentType\": \"GOVERNMENT_ID\",   \"fileName\": \"drivers_license.jpg\" }
    ]
  }" | jq .
