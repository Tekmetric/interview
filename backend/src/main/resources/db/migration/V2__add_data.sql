-- The password is hash("password").
-- This is for demo purpose only. In prod, should not have hashed password in migration script.
INSERT INTO customer (id, first_name, last_name, email, password, role) VALUES
('550e8400-e29b-41d4-a716-446655440000', 'Admin', 'Staff', 'admin@example.com', '$2a$10$eaL0IQoCyvofGqC.Fn1z7.Szyh4jXCeHcBvOAkuss6j3Ww8gQMhjm', 'ADMIN'),
('550e8400-e29b-41d4-a716-446655440001', 'Cole', 'Palmer', 'x@example.com', '$2a$10$eaL0IQoCyvofGqC.Fn1z7.Szyh4jXCeHcBvOAkuss6j3Ww8gQMhjm', 'USER'),
('550e8400-e29b-41d4-a716-446655440002', null, 'Becker', 'y@example.com', '$2a$10$eaL0IQoCyvofGqC.Fn1z7.Szyh4jXCeHcBvOAkuss6j3Ww8gQMhjm', 'USER'),
('550e8400-e29b-41d4-a716-446655440003', 'Bukayo', 'Saka', 'z@example.com', '$2a$10$eaL0IQoCyvofGqC.Fn1z7.Szyh4jXCeHcBvOAkuss6j3Ww8gQMhjm', 'USER'),
('550e8400-e29b-41d4-a716-446655440004', 'Mo', 'Salah', 'w@example.com', '$2a$10$eaL0IQoCyvofGqC.Fn1z7.Szyh4jXCeHcBvOAkuss6j3Ww8gQMhjm', 'USER');

-- Insert addresses for customers
-- Admin Staff: 1 address
INSERT INTO addresses (id, street, city, zip, state, customer_id) VALUES
('660e8400-e29b-41d4-a716-446655440000', '123 Admin Street', 'New York', '10001', 'NY', '550e8400-e29b-41d4-a716-446655440000');

-- Cole Palmer: 1 address  
INSERT INTO addresses (id, street, city, zip, state, customer_id) VALUES
('660e8400-e29b-41d4-a716-446655440001', '456 Palmer Avenue', 'Los Angeles', '90210', 'CA', '550e8400-e29b-41d4-a716-446655440001');

-- Becker: 2 addresses
INSERT INTO addresses (id, street, city, zip, state, customer_id) VALUES
('660e8400-e29b-41d4-a716-446655440002', '789 Becker Road', 'Chicago', '60601', 'IL', '550e8400-e29b-41d4-a716-446655440002'),
('660e8400-e29b-41d4-a716-446655440003', '321 Second Home Blvd', 'Miami', '33101', 'FL', '550e8400-e29b-41d4-a716-446655440002');

-- Bukayo Saka: 2 addresses
INSERT INTO addresses (id, street, city, zip, state, customer_id) VALUES
('660e8400-e29b-41d4-a716-446655440004', '555 Saka Lane', 'Seattle', '98101', 'WA', '550e8400-e29b-41d4-a716-446655440003'),
('660e8400-e29b-41d4-a716-446655440005', '777 Arsenal Way', 'Portland', '97201', 'OR', '550e8400-e29b-41d4-a716-446655440003');

-- Mo Salah: 0 addresses (no insert needed)