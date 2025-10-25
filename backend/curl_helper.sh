curl -X POST http://localhost:8080/api/v1/vehicle \
     -H "Content-Type: application/json" \
     -d '{"mileage":9001,"year":2016,"make":"Chevrolet","model":"Silverado LT","color":"Black","licensePlate":"DEF678","vin":"1234567899","owner":"Lucas"}'
