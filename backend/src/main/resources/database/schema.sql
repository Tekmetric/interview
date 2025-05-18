-- Provide SQL scripts here
DROP TABLE IF EXISTS vehicles;

CREATE TABLE vehicles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR NOT NULL,
    fabrication_year SMALLINT,
    make VARCHAR NOT NULL,
    model VARCHAR NOT NULL
);