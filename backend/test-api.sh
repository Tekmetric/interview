#!/bin/bash

# API Testing Script for User CRUD Operations
# Make sure the application is running on http://localhost:8080

BASE_URL="http://localhost:8080/api/user"
# Credentials for Basic Auth (must match the created user's credentials)
AUTH_USER="johndoe"
AUTH_PASS='SecurePass123$'

echo "========================================="
echo "Testing User CRUD API"
echo "========================================="
echo ""

echo "1. CREATE (EXPECTED FAIL) - Creating a user with null firstName..."
# Attempt to create a user with a null required field to trigger validation error (HTTP 400)
FAIL_BODY=$(curl -s -X POST $BASE_URL \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "password": "SecurePass123$",
    "firstName": null,
    "middleName": "Michael",
    "lastName": "Doe",
    "dateOfBirth": "1990-01-15",
    "ssn": "360067890",
    "gender": "MALE",
    "email": "john.doe@example.com",
    "phoneNumber": "5550108888"
  }')
FAIL_STATUS=$(curl -s -o /dev/null -w "%{http_code}" -X POST $BASE_URL \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "password": "SecurePass123$",
    "firstName": null,
    "middleName": "Michael",
    "lastName": "Doe",
    "dateOfBirth": "1990-01-15",
    "ssn": "360067890",
    "gender": "MALE",
    "email": "john.doe@example.com",
    "phoneNumber": "5550108888"
  }')

echo "Response (HTTP $FAIL_STATUS):"
echo "$FAIL_BODY"
echo ""

echo "2. CREATE (SUCCESS) - Creating a new user..."
CREATE_RESPONSE=$(curl -s -X POST $BASE_URL \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "password": "SecurePass123$",
    "firstName": "John",
    "middleName": "Michael",
    "lastName": "Doe",
    "dateOfBirth": "1990-01-15",
    "ssn": "360067890",
    "gender": "MALE",
    "email": "john.doe@example.com",
    "phoneNumber": "5550108888"
  }')

echo "Response:"
echo "$CREATE_RESPONSE"
echo ""

# Extract the ID from the response (assuming JSON response)
USER_ID=$(echo "$CREATE_RESPONSE" | grep -o '"id":[0-9]*' | grep -o '[0-9]*' | head -1)

if [ -z "$USER_ID" ]; then
    echo "Error: Could not extract user ID from response"
    USER_ID=1  # Fallback to ID 1
fi

echo "Created user with ID: $USER_ID"
echo ""

echo "3. READ (GET BY ID) - Fetching user with ID $USER_ID..."
curl -s -X GET "$BASE_URL/$USER_ID" -u "$AUTH_USER:$AUTH_PASS"
echo ""

echo "4. UPDATE - Updating user with ID $USER_ID..."
UPDATE_RESPONSE=$(curl -s -X PUT "$BASE_URL/$USER_ID" -u "$AUTH_USER:$AUTH_PASS" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Jane",
    "lastName": "Smith",
    "email": "jane.smith@example.com"
  }')

echo "Response:"
echo "$UPDATE_RESPONSE"
echo ""

# =========================================
# Bank API tests with Basic Authentication
# =========================================
BANK_BASE_URL="http://localhost:8080/api/bank"

echo "BANK 1. CREATE - Creating a new bank account for $AUTH_USER..."
BANK_CREATE_RESPONSE=$(curl -s -X POST $BANK_BASE_URL \
  -u "$AUTH_USER:$AUTH_PASS" \
  -H "Content-Type: application/json" \
  -d '{"accountNumber":"111122223333","routingNumber":"987654321"}')

echo "Response:"
echo "$BANK_CREATE_RESPONSE"
echo ""

# Extract BANK_ID from response
BANK_ID=$(echo "$BANK_CREATE_RESPONSE" | grep -o '"id":[0-9]*' | grep -o '[0-9]*' | head -1)
if [ -z "$BANK_ID" ]; then
  echo "Error: Could not extract bank ID from response"
  BANK_ID=1
fi

echo "Created bank with ID: $BANK_ID"
echo ""

echo "BANK 2. LIST - Fetching all bank accounts for $AUTH_USER..."
curl -s -X GET $BANK_BASE_URL \
  -u "$AUTH_USER:$AUTH_PASS"
echo ""

echo "BANK 3. GET BY ID - Fetching bank with ID $BANK_ID..."
curl -s -X GET "$BANK_BASE_URL/$BANK_ID" \
  -u "$AUTH_USER:$AUTH_PASS"
echo ""

echo "BANK 4. UPDATE - Updating bank account number for ID $BANK_ID..."
BANK_UPDATE_RESPONSE=$(curl -s -X PUT "$BANK_BASE_URL/$BANK_ID" \
  -u "$AUTH_USER:$AUTH_PASS" \
  -H "Content-Type: application/json" \
  -d '{"accountNumber":"444455556666"}')

echo "Response:"
echo "$BANK_UPDATE_RESPONSE"
echo ""

echo "BANK 5. SOFT DELETE - Soft deleting bank with ID $BANK_ID..."
BANK_DELETE_STATUS=$(curl -s -o /dev/null -w "%{http_code}" -X DELETE "$BANK_BASE_URL/$BANK_ID" -u "$AUTH_USER:$AUTH_PASS")
echo "Response: HTTP status $BANK_DELETE_STATUS"
echo ""

echo "BANK 6. VERIFY DELETE - Attempting to fetch soft-deleted bank..."
curl -i -s -X GET "$BANK_BASE_URL/$BANK_ID" -u "$AUTH_USER:$AUTH_PASS" | sed -n '1,10p'
echo ""

echo "BANK 7. LIST AFTER DELETE - Fetching all bank accounts again (should not include deleted)..."
curl -s -X GET $BANK_BASE_URL \
  -u "$AUTH_USER:$AUTH_PASS"
echo ""

echo "Skipping user DELETE tests: endpoint not implemented in API (only GET/PUT by ID are secured)."
echo ""

echo "========================================="
echo "API Testing Complete!"
echo "========================================="

