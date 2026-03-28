CREATE TABLE FRUITS
(
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    name                VARCHAR(255) NOT NULL,
    batch_number        VARCHAR(255),
    color               VARCHAR(255),
    origin_country      VARCHAR(255),
    category            VARCHAR(255),
    supplier            VARCHAR(255),
    organic             BOOLEAN,
    quantity            INT DEFAULT 0,
    registration_date   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified_date  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT unique_name_batch UNIQUE (name, supplier, batch_number)
);
