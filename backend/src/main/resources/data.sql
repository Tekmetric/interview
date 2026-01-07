-- DML: Insert Sample Data

-- Insert Customers
INSERT INTO customer (first_name, last_name, email, phone_number) VALUES
('John', 'Doe', 'john.doe@example.com', '555-0101'),
('Jane', 'Smith', 'jane.smith@example.com', '555-0102'),
('Bob', 'Johnson', 'bob.johnson@example.com', '555-0103'),
('Alice', 'Williams', 'alice.williams@example.com', '555-0104');

-- Insert Vehicles
INSERT INTO vehicle (make, model, model_year, vin, customer_id) VALUES
('Toyota', 'Camry', 2020, '1HGBH41JXMN109186', 1),
('Honda', 'Civic', 2019, '2HGFG12678H504567', 1),
('Ford', 'F-150', 2021, '1FTFW1ET5DKE12345', 2),
('Chevrolet', 'Malibu', 2018, '1G1ZD5ST0JF123456', 3),
('Tesla', 'Model 3', 2022, '5YJ3E1EA6KF123456', 4);
