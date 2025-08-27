CREATE TABLE vehicle
(
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_date       TIMESTAMP NOT NULL,
    last_modified_date TIMESTAMP NOT NULL,
    created_by         VARCHAR(255) NOT NULL,
    last_modified_by   VARCHAR(255) NOT NULL,
    type               VARCHAR(15) NOT NULL,
    production_year    INT          NOT NULL,
    vin                VARCHAR(17)  NOT NULL UNIQUE,
    model              VARCHAR(255) NOT NULL,
    make               VARCHAR(255) NOT NULL
);

CREATE INDEX idx_vehicle_type ON vehicle (type);
CREATE INDEX idx_vehicle_production_year ON vehicle (production_year);