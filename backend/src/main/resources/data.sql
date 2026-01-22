-- Spring Boot will automatically run this file on startup.

-- Create Customers
INSERT INTO customer (first_name, last_name, email) VALUES
('John', 'Doe', 'john.doe@example.com'),
('Jane', 'Smith', 'jane.smith@example.com');
-- Create Vehicles
-- John Doe's Vehicles
INSERT INTO vehicle (vin, make, model, model_year, customer_id) VALUES
('VIN1234567890', 'Toyota', 'Camry', 2021, 1),
('VIN0987654321', 'Honda', 'CRV', 2022, 1);

-- Jane Smith's Vehicle
INSERT INTO vehicle (vin, make, model, model_year, customer_id) VALUES
('VINABCDEFGHIJ', 'Ford', 'F-150', 2020, 2);

-- Create Service Jobs
-- Service Jobs for Toyota Camry
INSERT INTO service_job (description, creation_date, status, cost, vehicle_id) VALUES
('Oil Change', '2023-10-01T10:00:00Z', 'COMPLETED', 150.00, 1),
('Tire Rotation', '2023-11-15T14:30:00Z', 'PENDING', 75.50, 1);

-- Service Jobs for Honda CRV
INSERT INTO service_job (description, creation_date, status, cost, vehicle_id) VALUES
('Brake Inspection', '2023-10-20T09:00:00Z', 'IN_PROGRESS', 250.75, 2);

-- Service Jobs for Ford F-150
INSERT INTO service_job (description, creation_date, status, cost, vehicle_id) VALUES
('Annual Inspection', '2023-12-01T11:00:00Z', 'PENDING', 350.00, 3);
