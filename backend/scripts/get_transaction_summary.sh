#!/bin/bash

DEFAULT_CUSTOMER_ID=2a0b37e8-ad3d-4469-a4f0-c4ddc06ac65f
DEFAULT_REWARDS_ACCOUNT=$(curl -s -X POST "http://localhost:8080/rewards/$DEFAULT_CUSTOMER_ID/enroll" | tr -d '\n "')
curl -X GET -i "http://localhost:8080/transactions/${1:-$DEFAULT_REWARDS_ACCOUNT}/summary"