DROP TABLE IF EXISTS car_customer;
DROP TABLE IF EXISTS customer;
DROP TABLE IF EXISTS car;


CREATE TABLE car
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    make       VARCHAR(250) NOT NULL,
    model      VARCHAR(250) NOT NULL,
    model_year INT          NOT NULL,
    color      VARCHAR(250) DEFAULT NULL,
    vin        VARCHAR(17)  NOT NULL,
    UNIQUE (vin)
);

CREATE TABLE customer
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name  VARCHAR(255) NOT NULL,
    email      VARCHAR(255) NOT NULL,
    UNIQUE (email)
);

CREATE TABLE car_customer
(
    car_id      BIGINT NOT NULL,
    customer_id BIGINT NOT NULL,
    PRIMARY KEY (car_id, customer_id),
    FOREIGN KEY (car_id) REFERENCES car (id) ON DELETE CASCADE,
    FOREIGN KEY (customer_id) REFERENCES customer (id) ON DELETE CASCADE
);