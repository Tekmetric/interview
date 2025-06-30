-- Flyway migration V1: Create shop table
CREATE TABLE shop (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(255),
    number_of_employees INTEGER NOT NULL
);
