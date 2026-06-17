-- V6__Create_service_packages_table.sql
-- Migration to create service packages and customer subscriptions

-- Create service_packages table
CREATE TABLE service_packages
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(100)   NOT NULL UNIQUE,
    description   TEXT,
    monthly_price DECIMAL(10, 2) NOT NULL,
    active        BOOLEAN        NOT NULL DEFAULT TRUE,
    created_date  TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_date  TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by    VARCHAR(100),
    updated_by    VARCHAR(100)
);

-- Create junction table for many-to-many relationship
CREATE TABLE customer_service_packages
(
    customer_id        BIGINT NOT NULL,
    service_package_id BIGINT NOT NULL,
    PRIMARY KEY (customer_id, service_package_id),
    FOREIGN KEY (customer_id) REFERENCES customers (id) ON DELETE CASCADE,
    FOREIGN KEY (service_package_id) REFERENCES service_packages (id) ON DELETE CASCADE
);

-- Create indexes for better performance
CREATE INDEX idx_service_packages_name ON service_packages (name);
CREATE INDEX idx_service_packages_active ON service_packages (active);
CREATE INDEX idx_customer_service_packages_customer_id ON customer_service_packages (customer_id);
CREATE INDEX idx_customer_service_packages_service_package_id ON customer_service_packages (service_package_id);