-- Insert Roles/Users
INSERT INTO users (username, password, first_name, last_name, email_address, role, created_at, updated_at) VALUES ('admin', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Admin', 'User', 'admin@example.com', 'ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO users (username, password, first_name, last_name, email_address, role, created_at, updated_at) VALUES ('customer1', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'John', 'Doe', 'customer1@example.com', 'CUSTOMER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO users (username, password, first_name, last_name, email_address, role, created_at, updated_at) VALUES ('customer2', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Jane', 'Smith', 'customer2@example.com', 'CUSTOMER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert Vehicles
INSERT INTO vehicles (brand, model, registration_year, license_plate, owner_id, created_at, updated_at) VALUES ('Toyota', 'Camry', 2022, 'ABC-123', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO vehicles (brand, model, registration_year, license_plate, owner_id, created_at, updated_at) VALUES ('Honda', 'Civic', 2021, 'XYZ-789', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO vehicles (brand, model, registration_year, license_plate, owner_id, created_at, updated_at) VALUES ('Ford', 'F-150', 2023, 'LMN-456', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
