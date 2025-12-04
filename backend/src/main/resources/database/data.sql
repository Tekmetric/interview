CREATE TABLE IF NOT EXISTS customer (
id BIGINT AUTO_INCREMENT PRIMARY KEY,
name VARCHAR(255) NOT NULL,
email VARCHAR(255) NOT NULL
);


INSERT INTO customer (name, email) VALUES ('Alice', 'alice@example.com');
INSERT INTO customer (name, email) VALUES ('Bob', 'bob@example.com');