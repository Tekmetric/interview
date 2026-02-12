-- V5__Insert_vehicle_sample_data.sql
-- Insert sample vehicle data for development and testing

-- Insert sample vehicles for existing customers
INSERT INTO vehicles (customer_id, vin, make, model, vehicle_year, created_date, updated_date, created_by, updated_by)
VALUES
    -- John Doe's vehicles
    (1, '1HGCM82633A123456', 'Honda', 'Accord', 2018, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM'),
    (1, '2T1BURHE0JC654321', 'Toyota', 'Corolla', 2020, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM'),

    -- Jane Smith's vehicles
    (2, '1C4HJWEG5EL987654', 'Jeep', 'Grand Cherokee', 2019, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM'),

    -- Bob Johnson's vehicles
    (3, '1FTFW1ET5DFC78901', 'Ford', 'F-150', 2017, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM'),
    (3, '5NPE34AF4FH123789', 'Chevrolet', 'Silverado', 2015, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM');