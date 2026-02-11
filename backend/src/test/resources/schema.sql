DROP TABLE IF EXISTS autoshops;

CREATE TABLE autoshops (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(255),
    city VARCHAR(255),
    state VARCHAR(255),
    contract_years INT
);