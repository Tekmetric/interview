CREATE TABLE vehicle
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    mileage       INT          NOT NULL,
    model_year    INT          NOT NULL,
    make          VARCHAR(255) NOT NULL,
    model         VARCHAR(255) NOT NULL,
    color         VARCHAR(255),
    license_plate VARCHAR(255),
    vin           VARCHAR(255),
    owners_name   VARCHAR(255)
);
