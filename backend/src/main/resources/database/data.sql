--liquibase formatted sql

--changeset alex:create-model
CREATE TABLE snow_report
(
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    mountain_name      VARCHAR(255) NOT NULL,
    region             VARCHAR(255),
    country            VARCHAR(255) NOT NULL,
    current_snow_total INTEGER      NOT NULL,
    last_updated       TIMESTAMP    NOT NULL
);

--changeset alex:add-data
INSERT INTO snow_report (mountain_name, region, country, current_snow_total, last_updated)
VALUES ('Vail', 'Colorado', 'USA', 82, NOW());
INSERT INTO snow_report (mountain_name, region, country, current_snow_total, last_updated)
VALUES ('Park City', 'Utah', 'USA', 94, NOW());
INSERT INTO snow_report (mountain_name, region, country, current_snow_total, last_updated)
VALUES ('Whistler', 'British Columbia', 'Canada', 110, NOW());
