-- Start at 4 to skip the 3 inserts in data.sql
CREATE SEQUENCE vehicle_id_seq START WITH 4 INCREMENT BY 50;

CREATE TABLE vehicle (
    id BIGINT NOT NULL,
    model_year INTEGER NOT NULL, -- "year" is a reserved keyword
    make VARCHAR NOT NULL,
    model VARCHAR NOT NULL,
    color VARCHAR,
    license_plate VARCHAR,
    vin VARCHAR NOT NULL,
    fuel_type VARCHAR NOT NULL,
    doors INTEGER,
    mileage INTEGER,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT vehicle_pkey PRIMARY KEY (id),
    CONSTRAINT vehicle_year_check CHECK (model_year >= 1900),
    CONSTRAINT vehicle_doors_check CHECK (doors >= 0),
    CONSTRAINT vehicle_mileage_check CHECK (mileage >= 0),
    CONSTRAINT vehicle_vin_unique UNIQUE (vin)
);
