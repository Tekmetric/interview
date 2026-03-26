-- Provide SQL scripts here
-- API Keys
CREATE TABLE IF NOT EXISTS api_key (
    id          UUID            NOT NULL DEFAULT RANDOM_UUID(),
    name        VARCHAR(100)    NOT NULL,
    api_key     VARCHAR(255)    NOT NULL UNIQUE,
    active      BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_api_key PRIMARY KEY (id)
);

-- Vehicles
CREATE TABLE IF NOT EXISTS vehicle (
    id              UUID            NOT NULL,
    make            VARCHAR(100)    NOT NULL,
    model           VARCHAR(100)    NOT NULL,
    vehicle_year    INTEGER         NOT NULL,
    vin             VARCHAR(17)     NOT NULL UNIQUE,
    mileage         INTEGER         NOT NULL,
    created_at      TIMESTAMP       NOT NULL,
    updated_at      TIMESTAMP       NOT NULL,
    CONSTRAINT pk_vehicle PRIMARY KEY (id)
);

-- Indexes
CREATE INDEX IF NOT EXISTS idx_vehicle_make ON vehicle(make);
CREATE INDEX IF NOT EXISTS idx_vehicle_year ON vehicle(vehicle_year);
CREATE INDEX IF NOT EXISTS idx_vehicle_vin ON vehicle(vin);

-- Seed API keys
INSERT INTO api_key (id, name, api_key, active) VALUES
    (RANDOM_UUID(), 'test-client', 'test-secret-123', TRUE),
    (RANDOM_UUID(), 'postman-client', 'postman-secret-456', TRUE);

-- Seed vehicles
INSERT INTO vehicle (id, make, model, vehicle_year, vin, mileage, created_at, updated_at) VALUES
    (RANDOM_UUID(), 'Toyota', 'Camry', 2020, '4T1B11HK0KU123456', 15000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (RANDOM_UUID(), 'Honda', 'Civic', 2019, '2HGFC2F59KH123456', 32000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (RANDOM_UUID(), 'Ford', 'F-150', 2021, '1FTFW1ET5DKF12345', 8000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (RANDOM_UUID(), 'Chevrolet', 'Silverado', 2018, '3GCUKSEC4JG123456', 45000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (RANDOM_UUID(), 'BMW', '3 Series', 2022, 'WBA5R1C50KAK12345', 5000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);