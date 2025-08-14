
----------------------------------------------------------------------------------------------
--   This is obsolete because Flyway is used. Still keep this script to demo no-Flyway      --
----------------------------------------------------------------------------------------------


-- Drops the table if it already exists (helps in-memory reset)
--DROP TABLE IF EXISTS customer;
--
--CREATE TABLE customer (
--    id UUID PRIMARY KEY,
--    first_name VARCHAR(50),
--    last_name VARCHAR(50) NOT NULL,
--    email VARCHAR(50) UNIQUE NOT NULL,
--    password VARCHAR(25) NOT NULL,
--    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
--);
--
--INSERT INTO customer (id, first_name, last_name, email, password) VALUES
--(RANDOM_UUID(), 'Cole', 'Palmer', 'x@example.com', 'password'),
--(RANDOM_UUID(), null, 'Becker', 'y@example.com', 'password'),
--(RANDOM_UUID(), 'Bukayo', 'Saka', 'z@example.com', 'password');
--
--CREATE INDEX idx_customer_email ON customer(email);