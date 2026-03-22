CREATE SEQUENCE vehicle_id_seq START WITH 1 INCREMENT BY 50;

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
    CONSTRAINT vehicle_pkey PRIMARY KEY (id),
    CONSTRAINT vehicle_year_check CHECK (model_year >= 1900),
    CONSTRAINT vehicle_doors_check CHECK (doors >= 0),
    CONSTRAINT vehicle_mileage_check CHECK (mileage >= 0)
);

INSERT INTO vehicle (id, model_year, make, model, color, license_plate, vin, fuel_type, doors, mileage)
VALUES
    (nextval('vehicle_id_seq'), 2020, 'Toyota', 'Corolla', 'Silver', 'ABC123', 'JTDB4MEE9L1234567', 'GASOLINE', 4, 45000),
    (nextval('vehicle_id_seq'), 2018, 'Honda', 'Civic', 'Blue', 'XYZ789', '2HGFC2F69JH123456', 'HYBRID', 4, 72000),
    (nextval('vehicle_id_seq'), 2023, 'Tesla', 'Model 3', 'Red', 'TKMTRC', '5YJ3E1EA0MF123456', 'ELECTRIC', 4, 15000);