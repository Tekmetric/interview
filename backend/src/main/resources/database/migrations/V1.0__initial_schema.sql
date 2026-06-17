CREATE TABLE repair_order (
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
    vin                 VARCHAR (255) NOT NULL,
    car_model           VARCHAR (255) NOT NULL,
    issue_description   VARCHAR (255) NOT NULL,
    status              VARCHAR (255) NOT NULL,
    created_at          TIMESTAMP NOT NULL,
    updated_at          TIMESTAMP NOT NULL,
    is_deleted          BOOLEAN  DEFAULT FALSE
)