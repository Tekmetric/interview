-- Provide SQL scripts here
CREATE TABLE IF NOT EXISTS person(
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(15) NOT NULL,
    address_country_code VARCHAR(2) NOT NULL,
    address_postal_code VARCHAR(10) NOT NULL,
    address_administrative_area VARCHAR(100) NOT NULL,
    address_locality VARCHAR(100) NOT NULL,
    address_lines VARCHAR(100)[] NOT NULL
);
