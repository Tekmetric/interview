-- Spring Boot will automatically run this file on startup.
-- This script provides extensive data for pagination testing.

-- Create Customers (10 total)
INSERT INTO customer (first_name, last_name, email) VALUES
('John', 'Doe', 'john.doe@example.com'),             -- ID 1
('Jane', 'Smith', 'jane.smith@example.com'),         -- ID 2
('Alice', 'Johnson', 'alice.j@example.com'),         -- ID 3
('Bob', 'Williams', 'bob.w@example.com'),            -- ID 4
('Charlie', 'Brown', 'charlie.b@example.com'),       -- ID 5
('Diana', 'Miller', 'diana.m@example.com'),          -- ID 6
('Eve', 'Davis', 'eve.d@example.com'),               -- ID 7
('Frank', 'Garcia', 'frank.g@example.com'),          -- ID 8
('Grace', 'Rodriguez', 'grace.r@example.com'),       -- ID 9
('Henry', 'Martinez', 'henry.m@example.com');        -- ID 10

-- Create Vehicles (approx. 20 total)
-- Customer 1 (John Doe)
INSERT INTO vehicle (vin, make, model, model_year, customer_id) VALUES
('1G1FY2D37A7123456', 'Toyota', 'Camry', 2021, 1),    -- ID 1
('2G1RT2D37A7654321', 'Honda', 'CRV', 2022, 1),      -- ID 2
('3G1BE2D37A7ABCDEF', 'Ford', 'Mustang', 2018, 1);   -- ID 3

-- Customer 2 (Jane Smith)
INSERT INTO vehicle (vin, make, model, model_year, customer_id) VALUES
('4G1HJ2D37A7GHIJKL', 'Ford', 'F-150', 2020, 2),     -- ID 4
('5G1KL2D37A7MNOPQR', 'BMW', 'X5', 2023, 2);         -- ID 5

-- Customer 3 (Alice Johnson)
INSERT INTO vehicle (vin, make, model, model_year, customer_id) VALUES
('6G1MN2D37A7STUVWX', 'Mercedes', 'C-Class', 2024, 3), -- ID 6
('7G1PQ2D37A7YZABCD', 'Audi', 'A4', 2019, 3);        -- ID 7

-- Customer 4 (Bob Williams)
INSERT INTO vehicle (vin, make, model, model_year, customer_id) VALUES
('8G1RS2D37A7EFGHIJ', 'Tesla', 'Model 3', 2023, 4);   -- ID 8

-- Customer 5 (Charlie Brown)
INSERT INTO vehicle (vin, make, model, model_year, customer_id) VALUES
('9G1TU2D37A7KLMNOP', 'Nissan', 'Rogue', 2021, 5);    -- ID 9

-- Customer 6 (Diana Miller)
INSERT INTO vehicle (vin, make, model, model_year, customer_id) VALUES
('A12VW2D37A7QRSTUV', 'Subaru', 'Outback', 2022, 6);   -- ID 10

-- Customer 7 (Eve Davis)
INSERT INTO vehicle (vin, make, model, model_year, customer_id) VALUES
('B12XY2D37A7WXYZAB', 'Hyundai', 'Tucson', 2023, 7);  -- ID 11

-- Customer 8 (Frank Garcia)
INSERT INTO vehicle (vin, make, model, model_year, customer_id) VALUES
('C12ZA2D37A7CDEFGH', 'Kia', 'Seltos', 2022, 8);      -- ID 12

-- Customer 9 (Grace Rodriguez)
INSERT INTO vehicle (vin, make, model, model_year, customer_id) VALUES
('D12BC2D37A7IJKLMN', 'Volvo', 'XC60', 2024, 9);      -- ID 13

-- Customer 10 (Henry Martinez)
INSERT INTO vehicle (vin, make, model, model_year, customer_id) VALUES
('E12DE2D37A7OPQRST', 'Mazda', 'CX-5', 2021, 10);     -- ID 14
INSERT INTO vehicle (vin, make, model, model_year, customer_id) VALUES
('F12FG2D37A7UVWXYX', 'Lexus', 'RX 350', 2023, 10);   -- ID 15

-- Create Service Jobs (approx. 25 total)
-- Vehicle 1 (Toyota Camry) - Customer 1
INSERT INTO service_job (description, creation_date, status, cost, vehicle_id) VALUES
('Oil Change', '2023-10-01T10:00:00Z', 'COMPLETED', 150.00, 1), -- ID 1
('Tire Rotation', '2023-11-15T14:30:00Z', 'PENDING', 75.50, 1),  -- ID 2
('Brake Pad Replacement', '2023-12-01T09:00:00Z', 'IN_PROGRESS', 400.00, 1); -- ID 3

-- Vehicle 2 (Honda CRV) - Customer 1
INSERT INTO service_job (description, creation_date, status, cost, vehicle_id) VALUES
('Engine Diagnostic', '2023-09-20T11:00:00Z', 'COMPLETED', 100.00, 2), -- ID 4
('AC Recharge', '2023-11-05T13:00:00Z', 'COMPLETED', 200.00, 2);      -- ID 5

-- Vehicle 4 (Ford F-150) - Customer 2
INSERT INTO service_job (description, creation_date, status, cost, vehicle_id) VALUES
('Annual Inspection', '2023-12-01T11:00:00Z', 'PENDING', 350.00, 4), -- ID 6
('Transmission Flush', '2023-11-20T10:00:00Z', 'IN_PROGRESS', 600.00, 4); -- ID 7

-- Vehicle 5 (BMW X5) - Customer 2
INSERT INTO service_job (description, creation_date, status, cost, vehicle_id) VALUES
('Oil Change', '2023-10-10T08:00:00Z', 'COMPLETED', 250.00, 5); -- ID 8

-- Vehicle 6 (Mercedes C-Class) - Customer 3
INSERT INTO service_job (description, creation_date, status, cost, vehicle_id) VALUES
('Pre-Sale Detail', '2024-01-10T15:00:00Z', 'PENDING', 300.00, 6); -- ID 9
INSERT INTO service_job (description, creation_date, status, cost, vehicle_id) VALUES
('Wheel Alignment', '2024-01-12T10:00:00Z', 'PENDING', 120.00, 6); -- ID 10

-- Vehicle 7 (Audi A4) - Customer 3
INSERT INTO service_job (description, creation_date, status, cost, vehicle_id) VALUES
('Tire Replacement', '2023-08-01T14:00:00Z', 'COMPLETED', 800.00, 7); -- ID 11
INSERT INTO service_job (description, creation_date, status, cost, vehicle_id) VALUES
('Brake Fluid Change', '2024-01-05T09:00:00Z', 'PENDING', 150.00, 7); -- ID 12

-- Vehicle 8 (Tesla Model 3) - Customer 4
INSERT INTO service_job (description, creation_date, status, cost, vehicle_id) VALUES
('Software Update', '2024-01-15T11:00:00Z', 'COMPLETED', 0.00, 8); -- ID 13
INSERT INTO service_job (description, creation_date, status, cost, vehicle_id) VALUES
('Tire Rotation', '2024-01-20T10:00:00Z', 'PENDING', 70.00, 8); -- ID 14

-- Vehicle 9 (Nissan Rogue) - Customer 5
INSERT INTO service_job (description, creation_date, status, cost, vehicle_id) VALUES
('Oil Change', '2023-07-01T09:00:00Z', 'COMPLETED', 130.00, 9); -- ID 15

-- Vehicle 10 (Subaru Outback) - Customer 6
INSERT INTO service_job (description, creation_date, status, cost, vehicle_id) VALUES
('100k Mile Service', '2024-01-01T08:00:00Z', 'IN_PROGRESS', 1200.00, 10); -- ID 16

-- Vehicle 11 (Hyundai Tucson) - Customer 7
INSERT INTO service_job (description, creation_date, status, cost, vehicle_id) VALUES
('Brake Inspection', '2023-11-10T14:00:00Z', 'COMPLETED', 50.00, 11); -- ID 17

-- Vehicle 12 (Kia Seltos) - Customer 8
INSERT INTO service_job (description, creation_date, status, cost, vehicle_id) VALUES
('Winter Tire Swap', '2023-10-25T13:00:00Z', 'COMPLETED', 100.00, 12); -- ID 18

-- Vehicle 13 (Volvo XC60) - Customer 9
INSERT INTO service_job (description, creation_date, status, cost, vehicle_id) VALUES
('Full Diagnostic', '2024-01-18T10:00:00Z', 'PENDING', 200.00, 13); -- ID 19

-- Vehicle 14 (Mazda CX-5) - Customer 10
INSERT INTO service_job (description, creation_date, status, cost, vehicle_id) VALUES
('Oil Change', '2023-12-15T09:00:00Z', 'COMPLETED', 140.00, 14); -- ID 20

-- Vehicle 15 (Lexus RX 350) - Customer 10
INSERT INTO service_job (description, creation_date, status, cost, vehicle_id) VALUES
('Scheduled Maintenance', '2024-01-22T12:00:00Z', 'PENDING', 450.00, 15); -- ID 21