#!/usr/bin/env bash
# POST /api/v1/credit-applications/:id/confirm-documents
# Verifies that all expected supporting documents have been uploaded to S3 via HeadObject.
# Call this after completing all presigned PUT uploads (05b_upload_document.sh).
#
# On success (200): returns the application with documentDownloadUrls[] populated.
# On failure (422): returns a ProblemDetail listing the document types not yet in S3.
#
# Usage:
#   # Confirm documents for Jane's seeded application (SUBMITTED)
#   ./curl/credit_applications/05c_confirm_documents.sh
#
#   # Confirm documents for a newly created application
#   APP_ID=$(./curl/credit_applications/05_create.sh | jq -r '.id') \
#     ./curl/credit_applications/05c_confirm_documents.sh

APP_ID="${APP_ID:-018fae20-0000-7000-8000-000000000001}"

curl -s -u "api-user:changeme" \
  -X POST "http://localhost:8080/api/v1/credit-applications/$APP_ID/confirm-documents" | jq .
