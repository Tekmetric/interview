-- V4__Create_vehicles_table.sql
-- Migration to create vehicles table with relationship to customers

-- Create vehicles table
CREATE TABLE vehicles
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id  BIGINT      NOT NULL,
    vin          VARCHAR(17) NOT NULL UNIQUE,
    make         VARCHAR(50) NOT NULL,
    model        VARCHAR(50) NOT NULL,
    vehicle_year INTEGER     NOT NULL,
    created_date TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by   VARCHAR(100),
    updated_by   VARCHAR(100),
    FOREIGN KEY (customer_id) REFERENCES customers (id) ON DELETE CASCADE
);

-- Create indexes for better performance
CREATE INDEX idx_vehicles_customer_id ON vehicles (customer_id);
CREATE INDEX idx_vehicles_vin ON vehicles (vin);
CREATE INDEX idx_vehicles_make_model ON vehicles (make, model);