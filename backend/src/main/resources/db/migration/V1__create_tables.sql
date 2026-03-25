CREATE TABLE mechanic_shop
(
    mechanic_shop_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    shop_name        VARCHAR(255),
    phone_number     VARCHAR(50),
    email            VARCHAR(255),
    creation_date    TIMESTAMP,
    last_update_date TIMESTAMP
);


CREATE TABLE mechanic_shop_history
(
    mechanic_shop_history_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    mechanic_shop_id BIGINT,
    shop_name        VARCHAR(255),
    phone_number     VARCHAR(50),
    email            VARCHAR(255),
    action           VARCHAR(10),
    creation_date    TIMESTAMP,
    last_update_date TIMESTAMP,
    modified_by      VARCHAR(25)
);

CREATE TABLE mechanic
(
    mechanic_id      BIGINT PRIMARY KEY AUTO_INCREMENT,
    first_name       VARCHAR(255),
    last_name        VARCHAR(255),
    phone_number     VARCHAR(50),
    email            VARCHAR(255),
    role             VARCHAR(50),
    mechanic_shop_id BIGINT,
    creation_date    TIMESTAMP,
    last_update_date TIMESTAMP,
    CONSTRAINT fk_mechanic_shop
        FOREIGN KEY (mechanic_shop_id)
            REFERENCES mechanic_shop (mechanic_shop_id)
);

CREATE TABLE mechanic_history
(
    mechanic_history_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    mechanic_id      BIGINT,
    first_name       VARCHAR(255),
    last_name        VARCHAR(255),
    phone_number     VARCHAR(50),
    email            VARCHAR(255),
    role             VARCHAR(50),
    mechanic_shop_id BIGINT,
    action           VARCHAR(10),
    creation_date    TIMESTAMP,
    last_update_date TIMESTAMP,
    modified_by      VARCHAR(25)
);

CREATE INDEX idx_mechanic_shop_id ON mechanic(mechanic_shop_id);
CREATE INDEX idx_mechanic_shop_role ON mechanic(mechanic_shop_id, role);
CREATE UNIQUE INDEX uk_mechanic_email ON mechanic(email);
CREATE UNIQUE INDEX uk_mechanic_shop_email ON mechanic_shop(email);
CREATE INDEX idx_mechanic_shop_name ON mechanic_shop(shop_name);
CREATE INDEX idx_mechanic_shop_history_shop_id ON mechanic_shop_history(mechanic_shop_history_id);

INSERT INTO mechanic_shop (mechanic_shop_id, shop_name, phone_number, email, creation_date, last_update_date)
VALUES (1, 'AutoFix Garage', '0711111111', 'contact@autofix.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (2, 'Speedy Repairs', '0722222222', 'office@speedy.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO mechanic (mechanic_id, first_name, last_name, phone_number, email, role, mechanic_shop_id, creation_date, last_update_date)
VALUES
-- Shop 1
(1, 'John', 'Doe', '0700000001', 'john.doe@mail.com', 'CHIEF_MECHANIC', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Mike', 'Smith', '0700000002', 'mike.smith@mail.com', 'MECHANIC', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'Alex', 'Popescu', '0700000003', 'alex.popescu@mail.com', 'APPRENTICE', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Shop 2
(4, 'Andrei', 'Ionescu', '0700000004', 'andrei.ionescu@mail.com', 'CHIEF_MECHANIC', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'Cristian', 'Georgescu', '0700000005', 'cristian.geo@mail.com', 'MECHANIC', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 'Vlad', 'Dumitrescu', '0700000006', 'vlad.d@mail.com', 'APPRENTICE', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);