-- Create Car table
CREATE TABLE car
(
    id       BIGINT PRIMARY KEY AUTO_INCREMENT,
    model    VARCHAR(255) NOT NULL,
    vin      VARCHAR(255) NOT NULL,
    owner_id BIGINT,
    version  BIGINT DEFAULT 0,
    CONSTRAINT fk_owner FOREIGN KEY (owner_id) REFERENCES owner (id),
    CONSTRAINT uk_car_vin UNIQUE (vin)
);