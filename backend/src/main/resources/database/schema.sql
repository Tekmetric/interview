-- Provide SQL scripts here

CREATE SCHEMA tkm;
SET SCHEMA tkm;

CREATE TABLE tkm.shops (
    id              BIGINT          AUTO_INCREMENT  PRIMARY KEY,
    name            VARCHAR(100)    NOT NULL UNIQUE,
    description     MEDIUMTEXT,
    address         VARCHAR(200)    NOT NULL,
    phone_no        VARCHAR(20),
    email           VARCHAR(50),
    active          BOOLEAN
)