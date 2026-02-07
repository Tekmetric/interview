CREATE TABLE IF NOT EXISTS vehicle (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    make VARCHAR(50) NOT NULL,
    model VARCHAR(50) NOT NULL,
    "year" INT NOT NULL,
    vin VARCHAR(17),
    license_plate VARCHAR(20),
    color VARCHAR(30),
    mileage INT,
    owner_name VARCHAR(100) NOT NULL,
    owner_phone VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO vehicle (make, model, "year", vin, license_plate, color, mileage, owner_name, owner_phone) VALUES
('Toyota', 'Camry', 2021, '1HGBH41JXMN109186', 'ABC-1234', 'Silver', 35000, 'John Smith', '555-0101');

INSERT INTO vehicle (make, model, "year", vin, license_plate, color, mileage, owner_name, owner_phone) VALUES
('Honda', 'Civic', 2020, '2HGFC2F53LH509876', 'XYZ-5678', 'Blue', 42000, 'Jane Doe', '555-0202');

INSERT INTO vehicle (make, model, "year", vin, license_plate, color, mileage, owner_name, owner_phone) VALUES
('Ford', 'F-150', 2022, '1FTEW1EP5LFA12345', 'DEF-9012', 'Black', 18000, 'Mike Johnson', '555-0303');

INSERT INTO vehicle (make, model, "year", vin, license_plate, color, mileage, owner_name, owner_phone) VALUES
('Chevrolet', 'Malibu', 2019, '1G1ZD5ST7KF234567', 'GHI-3456', 'White', 55000, 'Sarah Williams', '555-0404');

INSERT INTO vehicle (make, model, "year", vin, license_plate, color, mileage, owner_name, owner_phone) VALUES
('BMW', '330i', 2023, 'WBA5R1C57LA987654', 'JKL-7890', 'Gray', 8000, 'Robert Brown', '555-0505');