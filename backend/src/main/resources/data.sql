-- Insert some sample data only if the table is empty
INSERT INTO repair_services (customer_name, customer_phone, vehicle_make, vehicle_model, vehicle_year, license_plate, service_description, odometer_reading, status)
SELECT 'John Doe', '5551234567', 'Toyota', 'Camry', 2020, 'ABC123', 'Oil change and tire rotation', 35000, 'PENDING'
WHERE NOT EXISTS (SELECT 1 FROM repair_services LIMIT 1);

INSERT INTO repair_services (customer_name, customer_phone, vehicle_make, vehicle_model, vehicle_year, license_plate, service_description, odometer_reading, status)
SELECT 'Jane Smith', '5559876543', 'Honda', 'Accord', 2019, 'XYZ789', 'Brake pad replacement', 45000, 'IN_PROGRESS'
WHERE NOT EXISTS (SELECT 1 FROM repair_services LIMIT 1);

INSERT INTO repair_services (customer_name, customer_phone, vehicle_make, vehicle_model, vehicle_year, license_plate, service_description, odometer_reading, status)
SELECT 'Bob Johnson', '5552223333', 'Ford', 'F-150', 2018, 'DEF456', 'Engine diagnostic', 60000, 'DIAGNOSED'
WHERE NOT EXISTS (SELECT 1 FROM repair_services LIMIT 1);
