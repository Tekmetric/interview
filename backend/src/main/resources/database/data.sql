-- Table creation script for users and banks tables
-- Note: JPA will auto-create the tables if spring.jpa.hibernate.ddl-auto is set to create/update
-- This script is provided for reference

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(255),
    middle_name VARCHAR(255),
    last_name VARCHAR(255),
    username VARCHAR(255) UNIQUE,
    password VARCHAR(255),
    password_key VARCHAR(255),
    date_of_birth DATE,
    ssn VARCHAR(255),
    gender VARCHAR(20),
    email VARCHAR(255),
    phone_number VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS banks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_number VARCHAR(255),
    routing_number VARCHAR(255),
    deleted BOOLEAN DEFAULT FALSE NOT NULL,
    user_id BIGINT,
    CONSTRAINT fk_banks_users FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Sample data (optional)
-- INSERT INTO users (first_name, middle_name, last_name, date_of_birth, ssn, gender, email, phone_number)
-- VALUES ('John', 'Michael', 'Doe', '1990-01-15', '123-45-6789', 'MALE', 'john.doe@example.com', '555-0101');
-- INSERT INTO banks (account_number, routing_number, user_id) VALUES ('1234567890', '987654321', 1);