-- Provide SQL scripts here

CREATE SCHEMA tkm;
SET SCHEMA tkm;

CREATE TABLE tkm.shops (
    id      BIGINT          AUTO_INCREMENT  PRIMARY KEY,
    name    VARCHAR(100)    NOT NULL UNIQUE,
    address VARCHAR(200)    NOT NULL
)