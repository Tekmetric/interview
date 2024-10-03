#!/bin/bash

# Base URL for the API
API_URL="http://localhost:8080/api/popstars"
popstar_id=$((10 + RANDOM))


# Test Data
popstar_data="{
  \"id\": $popstar_id,
  \"firstName\": \"Ryan\",
  \"lastName\": \"Gosling\",
  \"born\": \"1980-11-12\",
  \"died\": null
}"

# 1. Test Create (POST)
echo "Creating new popstar..."
curl -X POST "$API_URL/create" -H "Content-Type: application/json" -d "$popstar_data"
echo -e "\n"


popstar_data="{
  \"id\": $popstar_id,
  \"firstName\": \"Ryan\",
  \"lastName\": \"Gosling\",
  \"born\": \"1980-11-12\",
  \"died\": \"2100-11-12\"
}"

# 2. Test Update (PUT)
echo "Updating popstar with ID $popstar_id..."
curl -X PUT "$API_URL/update" -H "Content-Type: application/json" -d "$popstar_data"
echo -e "\n"

# 3. Test Get All (GET)
echo "Fetching all popstars..."
curl -X GET "$API_URL/all"
echo -e "\n"

# 4. Test Get by ID (GET)
echo "Fetching popstar with ID $popstar_id..."
curl -X GET "$API_URL/$popstar_id"
echo -e "\n"

# 5. Test Delete (DELETE)
echo "Deleting popstar with ID $popstar_id..."
curl -X DELETE "$API_URL/delete/$popstar_id"
echo -e "\n"