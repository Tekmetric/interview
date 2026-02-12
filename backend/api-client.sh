#!/bin/bash
# api-client.sh - Demonstrate Project CRUD API

BASE_URL="http://localhost:8080/api/projects"
AUTH="admin:password"
PROJECT_UID=a1b2c3d4-e5f6-47b8-9a0b-112233445566

echo "===  Creating a new project  ==="
curl --user $AUTH --request POST "$BASE_URL" \
  --header "Content-Type: application/json" \
  --data '{
        "name": "Tekmetric",
        "description": "Car repair shop"
      }'
echo -e "\n\n"

echo "===  Listing all projects  ==="
curl --user $AUTH --request GET "$BASE_URL"
echo -e "\n\n"

echo "===  Get project by UID  ==="
curl --user $AUTH --request GET "$BASE_URL/$PROJECT_UID"
echo -e "\n\n"

echo "===  Updating the project  ==="
curl --user $AUTH --request PUT "$BASE_URL/$PROJECT_UID" \
  --header "Content-Type: application/json" \
  --data "{
        \"uid\": \"$PROJECT_UID\",
        \"name\": \"A new company!\",
        \"description\": \"A new business type!\",
        \"status\": \"PLANNED\"
      }"
echo -e "\n\n"

echo "===  Remove project by UID  ==="
curl --user $AUTH --request DELETE "$BASE_URL/$PROJECT_UID"
echo -e "\n\n"

echo "===  Get project by UID again ==="
curl --user $AUTH --request GET "$BASE_URL/$PROJECT_UID"
echo -e "\n\n"
