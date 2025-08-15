-- Drops the tables if they already exist (helps in-memory reset)
DROP TABLE IF EXISTS addresses;
DROP TABLE IF EXISTS customer;

CREATE TABLE customer (
    id UUID PRIMARY KEY,
    first_name VARCHAR(50),
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE addresses (
    id UUID PRIMARY KEY,
    street VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    zip VARCHAR(20) NOT NULL,
    state VARCHAR(50) NOT NULL,
    customer_id UUID,
    -- Cascade delete on DB level, not on JPA-level, which can cause N + 1 problem
    CONSTRAINT fk_address_customer FOREIGN KEY (customer_id) REFERENCES customer(id) ON DELETE CASCADE
);

CREATE INDEX idx_customer_email ON customer(email);
CREATE INDEX idx_address_customer_id ON addresses(customer_id);