-- Insert 100 customers
INSERT INTO customers (id, name, email, phone_number, address, created_date, updated_date)
VALUES
    (1, 'Customer 1', 'customer1@example.com', '1234567890', 'Address 1', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 'Customer 2', 'customer2@example.com', '1234567891', 'Address 2', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
-- Repeat for 100 customers
    (100, 'Customer 100', 'customer100@example.com', '1234567999', 'Address 100', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert 125 vehicles
INSERT INTO vehicles (id, customer_id, make, model, year, license_plate, created_date, updated_date)
VALUES
    (1, 1, 'Toyota', 'Corolla', 2015, 'ABC123', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 1, 'Honda', 'Civic', 2018, 'DEF456', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (3, 2, 'Ford', 'Focus', 2017, 'GHI789', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
-- Repeat for 125 vehicles
    (125, 100, 'Chevrolet', 'Malibu', 2020, 'XYZ999', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert 180 repair orders
INSERT INTO repair_orders (id, vehicle_id, description, status, created_date, updated_date)
VALUES
    (1, 1, 'Oil change', 'Completed', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 1, 'Brake replacement', 'In Progress', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (3, 2, 'Tire rotation', 'Completed', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
-- Repeat for 180 repair orders
    (180, 125, 'Engine diagnostics', 'Pending', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);