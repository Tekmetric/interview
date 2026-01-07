-- ============================================================================
-- CONSOLIDATED DDL + DML SCRIPT
-- ============================================================================
-- NOTE: For production-ready applications and better maintainability, it is
-- recommended to separate DDL (schema creation) into schema.sql and DML 
-- (data seeding) into data.sql. This allows for:
--   - Clear separation of concerns (structure vs. seed data)
--   - Independent schema migrations using tools like Flyway/Liquibase
--   - Ability to disable data seeding without affecting schema creation
--   - Better version control and change tracking
-- This consolidated file is for demo/testing convenience only.
-- ============================================================================

-- ============================================================================
-- DDL: Create Tables
-- ============================================================================

-- NOTE: I chose Long (Auto-Increment) IDs for ease of testing and readability during this demo.
-- In a production environment with public-facing URLs, I would use UUIDs to prevent resource enumeration attacks.
CREATE TABLE customer (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone_number VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- NOTE: I chose Long (Auto-Increment) IDs for ease of testing and readability during this demo.
-- In a production environment with public-facing URLs, I would use UUIDs to prevent resource enumeration attacks.
CREATE TABLE vehicle (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    make VARCHAR(100) NOT NULL,
    model VARCHAR(100) NOT NULL,
    model_year INT NOT NULL,
    vin VARCHAR(17) NOT NULL UNIQUE,
    customer_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_vehicle_customer FOREIGN KEY (customer_id) REFERENCES customer(id) ON DELETE CASCADE
);

-- ============================================================================
-- Indexes for performance optimization
-- ============================================================================

CREATE INDEX idx_customer_email ON customer(email);
CREATE INDEX idx_customer_last_name ON customer(last_name);
CREATE INDEX idx_customer_first_name ON customer(first_name);

CREATE INDEX idx_vehicle_vin ON vehicle(vin);
CREATE INDEX idx_vehicle_customer_id ON vehicle(customer_id);
CREATE INDEX idx_vehicle_model_year ON vehicle(model_year);

-- ============================================================================
-- DML: Insert Sample Data
-- ============================================================================

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
