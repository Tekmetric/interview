DROP TABLE IF EXISTS autoshop;

CREATE TABLE autoshop (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    address    VARCHAR(512) NOT NULL,
    phone      VARCHAR(32)  NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO autoshop (name, address, phone) VALUES
    ('Hopper Motors',     '123 Main St, Austin, TX',       '555-0100'),
    ('Maple St Auto',     '50 Maple St, Houston, TX',      '555-0101'),
    ('Gulf Coast Repair', '800 Coastal Rd, Galveston, TX', '555-0102');
