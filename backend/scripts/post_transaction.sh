#!/bin/bash

DEFAULT_CUSTOMER_ID="2a0b37e8-ad3d-4469-a4f0-c4ddc06ac65f"
DEFAULT_REWARDS_ACCOUNT=$(curl -s -X POST "http://localhost:8080/rewards/$DEFAULT_CUSTOMER_ID/enroll" | tr -d '\n "')
DEFAULT_AMOUNT="100.00"
DEFAULT_TYPE="PURCHASE"

REWARDS_ACCOUNT=${1:-$DEFAULT_REWARDS_ACCOUNT}
AMOUNT="${2:-$DEFAULT_AMOUNT}"
TYPE="${3:-$DEFAULT_TYPE}"
PURCHASE_ID="${4:-$(uuidgen)}"

HEADERS=(
  -H "Content-Type: application/json"
  -H "Accept: application/json"
)

REQUEST_BODY=$(jq -n \
  --arg pid "$PURCHASE_ID" \
  --arg rid "$REWARDS_ACCOUNT" \
  --arg amt "$AMOUNT" \
  --arg typ "$TYPE" \
  '{purchaseId: $pid, rewardsAccountId: $rid, amount: $amt, type: $typ}')

curl -i -X POST "http://localhost:8080/transactions" "${HEADERS[@]}" -d "$REQUEST_BODY"
