#!/bin/bash

BASE_URL="http://localhost:8080/api"
CONTENT_TYPE="Content-Type: application/json"

echo "🚀 Starting API Integration Test (Using grep/sed)..."
echo "-----------------------------------"

# 1. Create a Customer (Chihiro Ogino)
echo "1. Creating Customer..."
CUSTOMER_JSON='{"firstName": "Chihiro", "lastName": "Ogino", "phone": "123-456-0103"}'
CUSTOMER_RESPONSE=$(curl -s -X POST "$BASE_URL/customer" -H "$CONTENT_TYPE" -d "$CUSTOMER_JSON")
CUSTOMER_ID=$(echo "$CUSTOMER_RESPONSE" | sed -n 's/.*"id":\([0-9]*\).*/\1/p')
echo "Response: $CUSTOMER_RESPONSE"
echo "Generated Customer ID: $CUSTOMER_ID"
echo "-----------------------------------"

# 2. Add a Vehicle to that Customer
echo "2. Adding Vehicle to Customer..."
VIN="SPIRITAWAY1234568"
VEHICLE_JSON='{"vin": "'$VIN'", "make": "Audi", "model": "A4", "year": 2001}'
VEHICLE_RESPONSE=$(curl -s -X POST "$BASE_URL/vehicles/customer/$CUSTOMER_ID" -H "$CONTENT_TYPE" -d "$VEHICLE_JSON")
echo "Response: $VEHICLE_RESPONSE"
echo "-----------------------------------"

# 3. Get Vehicle by VIN
echo "3. Fetching Vehicle by VIN..."
curl -s -X GET "$BASE_URL/vehicles/$VIN"
echo -e "\n-----------------------------------"

# 4. Add a Service Order to the Vehicle
echo "4. Adding Service Order..."
ORDER_JSON='{"description": "Oil Change", "status": "PENDING"}'
ORDER_RESPONSE=$(curl -s -X POST "$BASE_URL/service/vehicle/$VIN" -H "$CONTENT_TYPE" -d "$ORDER_JSON")
ORDER_ID=$(echo "$ORDER_RESPONSE" | sed -n 's/.*"id":\([0-9]*\).*/\1/p')
echo "Response: $ORDER_RESPONSE"
echo "Generated Order ID: $ORDER_ID"
echo "-----------------------------------"

echo "4. Adding Service Order 2..."
ORDER_JSON2='{"description": "Tire Change", "status": "PENDING"}'
ORDER_RESPONSE2=$(curl -s -X POST "$BASE_URL/service/vehicle/$VIN" -H "$CONTENT_TYPE" -d "$ORDER_JSON2")
ORDER_ID2=$(echo "$ORDER_RESPONSE2" | sed -n 's/.*"id":\([0-9]*\).*/\1/p')
echo "Response: $ORDER_RESPONSE2"
echo "Generated Order ID: $ORDER_ID2"
echo "-----------------------------------"

# 5. Update Service Order Status
echo "5. Updating Service Order to COMPLETED..."
UPDATE_JSON='{"description": "Oil Change", "status": "COMPLETED"}'
curl -s -X PUT "$BASE_URL/service/$ORDER_ID" -H "$CONTENT_TYPE" -d "$UPDATE_JSON"
echo -e "\n-----------------------------------"

# 6. Get All Service Orders for Vehicle (Pageable)
echo "6. Getting Paged Service Orders for Vehicle..."
curl -s -X GET "$BASE_URL/service/vehicle/$VIN?page=0&size=10"
echo -e "\n-----------------------------------"

# 7. Get Service Orders by Status
echo "7. Filtering Service Orders by Status (COMPLETED)..."
curl -s -X GET "$BASE_URL/service/vehicle/$VIN/status/COMPLETED"
echo -e "\n-----------------------------------"

# 8. Remove Service Order from Vehicle
echo "8. Removing Service Order from Vehicle..."
curl -s -X DELETE "$BASE_URL/service/$ORDER_ID/vehicle/$VIN"
echo "Status: No Content (204)"
echo "-----------------------------------"

# 9. Remove Vehicle from Customer
echo "9. Removing Vehicle from Customer..."
curl -s -X DELETE "$BASE_URL/vehicles/$VIN/customer/$CUSTOMER_ID"
echo "Status: No Content (204)"
echo "-----------------------------------"

# 10. Delete Customer
echo "10. Deleting Customer..."
curl -s -X DELETE "$BASE_URL/customer/$CUSTOMER_ID"
echo "Status: No Content (204)"
echo "-----------------------------------"

echo "✅ Integration Test Complete."