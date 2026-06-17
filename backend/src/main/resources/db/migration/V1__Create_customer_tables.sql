-- V1__Create_customer_tables.sql
-- Initial migration to create customers and customer_profiles tables

-- Create customers table
CREATE TABLE customers
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name   VARCHAR(100) NOT NULL,
    last_name    VARCHAR(100) NOT NULL,
    email        VARCHAR(255) NOT NULL UNIQUE,
    phone        VARCHAR(20),
    created_date TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create customer_profiles table
CREATE TABLE customer_profiles
(
    id                       BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id              BIGINT NOT NULL UNIQUE,
    address                  TEXT,
    date_of_birth            DATE,
    preferred_contact_method VARCHAR(20) DEFAULT 'EMAIL',
    FOREIGN KEY (customer_id) REFERENCES customers (id) ON DELETE CASCADE
);

-- Create indexes for better performance
CREATE INDEX idx_customers_email ON customers (email);
CREATE INDEX idx_customer_profiles_customer_id ON customer_profiles (customer_id);