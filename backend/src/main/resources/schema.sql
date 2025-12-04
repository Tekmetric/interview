-- Schema for Customer table
-- This creates a simple customer table used by the interview backend application.
-- The email column is unique to demonstrate constraint and allow testing of
-- DataIntegrityViolation handling in the application.


CREATE TABLE IF NOT EXISTS customer (
id BIGINT AUTO_INCREMENT PRIMARY KEY,
name VARCHAR(255) NOT NULL,
email VARCHAR(255) NOT NULL,
CONSTRAINT uq_customer_email UNIQUE (email)
);