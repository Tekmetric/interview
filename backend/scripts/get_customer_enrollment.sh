#!/bin/bash
DEFAULT_CUSTOMER_ID="2a0b37e8-ad3d-4469-a4f0-c4ddc06ac65f"
curl -i -X GET "http://localhost:8080/rewards/${1:-$DEFAULT_CUSTOMER_ID}"
