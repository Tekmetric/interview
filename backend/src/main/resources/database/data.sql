CREATE TABLE IF NOT EXISTS vehicle (
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    make     VARCHAR(100) NOT NULL,
    model    VARCHAR(100) NOT NULL,
    year         INT       NOT NULL,
    vin      VARCHAR(17)  UNIQUE,
    mileage  INT          NOT NULL
);

INSERT INTO vehicle (make, model, year, vin, mileage) VALUES
    ('Toyota',  'Camry',   2020, '1HGBH41JXMN109186', 32000),
    ('Ford',    'F-150',   2019, '1FTFW1ET5DFC10312', 58000),
    ('Honda',   'Civic',   2021, '2HGFC2F52MH506789', 15000),
    ('Chevrolet','Silverado',2018,'3GCUKREC2JG123456', 74500),
    ('Tesla',   'Model 3', 2022, '5YJ3E1EA9NF123456', 8200);
