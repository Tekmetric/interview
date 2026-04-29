CREATE TABLE IF NOT EXISTS customer (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(100)        NOT NULL,
    last_name  VARCHAR(100)        NOT NULL,
    phone      VARCHAR(20),
    email      VARCHAR(255)        NOT NULL,
    created_at TIMESTAMP           NOT NULL,
    modified_at TIMESTAMP          NOT NULL
);

ALTER TABLE customer ADD COLUMN IF NOT EXISTS status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE';

-- Covers the default sort (last_name ASC, first_name ASC) and single-field sorts on either name column.
CREATE INDEX IF NOT EXISTS idx_customer_last_name_first_name ON customer (last_name, first_name);

-- Covers ORDER BY created_at and ORDER BY modified_at.
CREATE INDEX IF NOT EXISTS idx_customer_created_at  ON customer (created_at);
CREATE INDEX IF NOT EXISTS idx_customer_modified_at ON customer (modified_at);

-- Covers the status equality filter (?status=ACTIVE / INACTIVE).
CREATE INDEX IF NOT EXISTS idx_customer_status ON customer (status);

CREATE TABLE IF NOT EXISTS vehicle (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    vin         VARCHAR(17)         NOT NULL UNIQUE,
    make        VARCHAR(50)         NOT NULL,
    model       VARCHAR(50)         NOT NULL,
    year        INT                 NOT NULL,
    customer_id BIGINT              NOT NULL,
    created_at  TIMESTAMP           NOT NULL,
    modified_at TIMESTAMP           NOT NULL,
    active      BOOLEAN             NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_vehicle_customer FOREIGN KEY (customer_id) REFERENCES customer(id) ON DELETE CASCADE
);
