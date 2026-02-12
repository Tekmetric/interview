CREATE TABLE VEHICLES
(
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    vin                VARCHAR(255) NOT NULL UNIQUE,
    make               VARCHAR(255) NOT NULL,
    model              VARCHAR(255) NOT NULL,
    manufacture_year   INT          NOT NULL,
    license_plate      VARCHAR(255),
    owner_name         VARCHAR(255) NOT NULL,
    created_by         VARCHAR(255) NOT NULL,
    created_date       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified_by   VARCHAR(255),
    last_modified_date TIMESTAMP
);

CREATE INDEX idx_vin ON VEHICLES (vin);
CREATE INDEX idx_manufacture_year ON VEHICLES (manufacture_year);
CREATE INDEX idx_make_model ON VEHICLES (make, model);
