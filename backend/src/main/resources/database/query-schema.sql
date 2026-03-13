-- Query Database Schema
CREATE TABLE IF NOT EXISTS widgets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    version BIGINT DEFAULT 0
);
