-- Drops the table if it already exists (helps in-memory reset)
DROP TABLE IF EXISTS customer;

CREATE TABLE customer (
    id UUID PRIMARY KEY,
    first_name VARCHAR(50),
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_customer_email ON customer(email);