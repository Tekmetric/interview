# Test Steps

## This file contains the curl requests needed to go through the flow

### 1. Create a new shop

```shell
curl -X POST http://localhost:8080/api/shop \
     -H "Content-Type: application/json" \
     -d '{
           "name": "Acme Hardware",
           "address1": "123 Main St",
           "address2": "Suite 400",
           "city": "Springfield",
           "state": "IL",
           "zip": "62704",
           "phoneNumber": "5551234567",
           "email": "info@acmehardware.com",
           "website": "https://www.acmehardware.com"
         }'
```

### 2. Get singular shop (use ID from the curl response)

```shell
curl -X GET   http://localhost:8080/api/shop/1
```

### 3. Insert a second shop

```shell
curl -X POST http://localhost:8080/api/shop \
     -H "Content-Type: application/json" \
     -d '{
           "name": "Marcus Shop",
           "address1": "123 High St",
           "address2": "",
           "city": "Arlington",
           "state": "VA",
           "zip": "22201",
           "phoneNumber": "5551234567",
           "email": "info@marcusshop.com",
           "website": "https://www.marcusshop.com"
         }'
```

### 4. Return all shops

```shell
curl -X GET   http://localhost:8080/api/shops
```

### 5. Update the first shop 

```shell
curl -X PUT http://localhost:8080/api/shop/1 \
     -H "Content-Type: application/json" \
     -d '{
           "name": "Marcus Shop 2",
           "address1": "124 High St",
           "address2": "Address 2",
           "city": "Falls Church",
           "state": "VA",
           "zip": "22333",
           "phoneNumber": "5551234568",
           "email": "info2@marcusshop.com",
           "website": "https://www.marcusshop2.com"
         }'
```

### 6. Delete the first shop

```shell
curl -X DELETE http://localhost:8080/api/shop/1
```

### 7. Return all shops and verify the first shop is deleted

```shell
curl -X GET   http://localhost:8080/api/shops
```

### 8. Verify validations are working

```shell
curl -X POST http://localhost:8080/api/shop \
     -H "Content-Type: application/json" \
     -d '{
           "name": "",
           "address1": "",
           "address2": "",
           "city": "",
           "state": "",
           "zip": "",
           "phoneNumber": "",
           "email": "",
           "website": ""
         }'
```