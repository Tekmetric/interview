-- DDL: Create Tables
-- Note: I chose Long (Auto-Increment) IDs for ease of testing and readability during this demo.
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

-- Note: I chose Long (Auto-Increment) IDs for ease of testing and readability during this demo.
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

-- Indexes for performance optimization

CREATE INDEX idx_customer_email ON customer(email);
CREATE INDEX idx_customer_last_name ON customer(last_name);
CREATE INDEX idx_customer_first_name ON customer(first_name);

CREATE INDEX idx_vehicle_vin ON vehicle(vin);
CREATE INDEX idx_vehicle_customer_id ON vehicle(customer_id);
CREATE INDEX idx_vehicle_model_year ON vehicle(model_year);
