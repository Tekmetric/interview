#!/usr/bin/env bash
# PUT <presignedUrl> — upload a supporting document using a presigned S3 URL
# returned in documentUploadUrls[] from the 201 response of 05_create.sh.
#
# Each entry in documentUploadUrls has a documentType and a presignedUrl.
# Run this script once per document you want to upload.
#
# Locally (aws.enabled=false) the URL is a no-op placeholder; the PUT will return a
# connection error or 404 — this is expected. Against a real AWS environment the PUT
# would succeed with 200 and no response body.
#
# Usage:
#   # Upload the first document (PROOF_OF_INCOME) using a dummy payload
#   UPLOAD_URL=$(./05_create.sh | jq -r '.documentUploadUrls[0].presignedUrl') \
#     ./05b_upload_document.sh
#
#   # Upload with a real file
#   UPLOAD_URL="<paste presignedUrl>" DOCUMENT_FILE="/path/to/paystub.pdf" \
#     ./05b_upload_document.sh
#
#   # Upload all documents in a loop
#   RESPONSE=$(./05_create.sh)
#   echo "$RESPONSE" | jq -r '.documentUploadUrls[].presignedUrl' | while read -r url; do
#     UPLOAD_URL="$url" ./05b_upload_document.sh
#   done

if [ -z "$UPLOAD_URL" ]; then
  echo "ERROR: set UPLOAD_URL to a presignedUrl from documentUploadUrls[] in the create response"
  exit 1
fi

# If DOCUMENT_FILE is set, upload the real file; otherwise send a small inline dummy
# payload so the script is immediately runnable without preparing a file.
if [ -n "$DOCUMENT_FILE" ]; then
  curl -s -o /dev/null -w "HTTP %{http_code}\n" \
    --max-time 10 \
    -X PUT "$UPLOAD_URL" \
    -H "Content-Type: application/octet-stream" \
    --data-binary "@$DOCUMENT_FILE"
else
  curl -s -o /dev/null -w "HTTP %{http_code}\n" \
    --max-time 10 \
    -X PUT "$UPLOAD_URL" \
    -H "Content-Type: text/plain" \
    --data "Dummy document payload — set DOCUMENT_FILE=/path/to/file for a real upload"
fi
