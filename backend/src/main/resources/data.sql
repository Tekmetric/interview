-- Insert Users
-- Password for all users: "123456" (BCrypt hash: $2a$10$N3niKqzeoV2BWRMFLvFzaueiLqBnReBa.RR6Fw6tYXweVSYs/90jO)

-- Admin user
INSERT INTO users (username, password_hash, first_name, last_name, email_address, role, created_at, updated_at)
VALUES ('admin', '$2a$10$N3niKqzeoV2BWRMFLvFzaueiLqBnReBa.RR6Fw6tYXweVSYs/90jO', 'Admin', 'User', 'admin@example.com', 'ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Customer users (for testing different scenarios)
INSERT INTO users (username, password_hash, first_name, last_name, email_address, role, created_at, updated_at)
VALUES ('customer1', '$2a$10$N3niKqzeoV2BWRMFLvFzaueiLqBnReBa.RR6Fw6tYXweVSYs/90jO', 'John', 'Doe', 'john.doe@example.com', 'CUSTOMER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO users (username, password_hash, first_name, last_name, email_address, role, created_at, updated_at)
VALUES ('customer2', '$2a$10$N3niKqzeoV2BWRMFLvFzaueiLqBnReBa.RR6Fw6tYXweVSYs/90jO', 'Jane', 'Smith', 'jane.smith@example.com', 'CUSTOMER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO users (username, password_hash, first_name, last_name, email_address, role, created_at, updated_at)
VALUES ('customer3', '$2a$10$N3niKqzeoV2BWRMFLvFzaueiLqBnReBa.RR6Fw6tYXweVSYs/90jO', 'Bob', 'Johnson', 'bob.johnson@example.com', 'CUSTOMER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO users (username, password_hash, first_name, last_name, email_address, role, created_at, updated_at)
VALUES ('customer4', '$2a$10$N3niKqzeoV2BWRMFLvFzaueiLqBnReBa.RR6Fw6tYXweVSYs/90jO', 'Alice', 'Williams', 'alice.williams@example.com', 'CUSTOMER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Customer with no vehicles (for testing empty results)
INSERT INTO users (username, password_hash, first_name, last_name, email_address, role, created_at, updated_at)
VALUES ('customer5', '$2a$10$N3niKqzeoV2BWRMFLvFzaueiLqBnReBa.RR6Fw6tYXweVSYs/90jO', 'Charlie', 'Brown', 'charlie.brown@example.com', 'CUSTOMER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert Vehicles
-- Customer 1 (John Doe) - Multiple vehicles for pagination/filtering tests
INSERT INTO vehicles (brand, model, registration_year, license_plate, owner_id, created_at, updated_at)
VALUES ('Toyota', 'Camry', 2022, 'ABC-1234', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO vehicles (brand, model, registration_year, license_plate, owner_id, created_at, updated_at)
VALUES ('Honda', 'Civic', 2021, 'XYZ-7890', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO vehicles (brand, model, registration_year, license_plate, owner_id, created_at, updated_at)
VALUES ('Toyota', 'Corolla', 2023, 'TOY-2023', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO vehicles (brand, model, registration_year, license_plate, owner_id, created_at, updated_at)
VALUES ('Ford', 'Mustang', 2020, 'FORD-001', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Customer 2 (Jane Smith) - Different brands for filtering
INSERT INTO vehicles (brand, model, registration_year, license_plate, owner_id, created_at, updated_at)
VALUES ('BMW', '3 Series', 2023, 'BMW-3SER', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO vehicles (brand, model, registration_year, license_plate, owner_id, created_at, updated_at)
VALUES ('Mercedes-Benz', 'C-Class', 2022, 'MB-C200', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO vehicles (brand, model, registration_year, license_plate, owner_id, created_at, updated_at)
VALUES ('Audi', 'A4', 2021, 'AUDI-A4', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Customer 3 (Bob Johnson) - Mix of older and newer vehicles
INSERT INTO vehicles (brand, model, registration_year, license_plate, owner_id, created_at, updated_at)
VALUES ('Chevrolet', 'Silverado', 2019, 'CHEV-001', 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO vehicles (brand, model, registration_year, license_plate, owner_id, created_at, updated_at)
VALUES ('Nissan', 'Altima', 2024, 'NISS-2024', 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO vehicles (brand, model, registration_year, license_plate, owner_id, created_at, updated_at)
VALUES ('Hyundai', 'Elantra', 2022, 'HYUN-EL', 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Customer 4 (Alice Williams) - Single vehicle
INSERT INTO vehicles (brand, model, registration_year, license_plate, owner_id, created_at, updated_at)
VALUES ('Tesla', 'Model 3', 2023, 'TESLA-M3', 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO vehicles (brand, model, registration_year, license_plate, owner_id, created_at, updated_at)
VALUES ('Volkswagen', 'Jetta', 2021, 'VW-JETTA', 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Additional vehicles for comprehensive testing (more Toyota for brand filtering)
INSERT INTO vehicles (brand, model, registration_year, license_plate, owner_id, created_at, updated_at)
VALUES ('Toyota', 'RAV4', 2022, 'TOY-RAV4', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO vehicles (brand, model, registration_year, license_plate, owner_id, created_at, updated_at)
VALUES ('Toyota', 'Highlander', 2021, 'TOY-HIGH', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
