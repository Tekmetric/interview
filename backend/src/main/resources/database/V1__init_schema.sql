-- Create Owner table
CREATE TABLE owner
(
    id   BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL
);


-- Create Car table
CREATE TABLE car
(
    id       BIGINT PRIMARY KEY AUTO_INCREMENT,
    model    VARCHAR(255) NOT NULL,
    owner_id BIGINT,
    CONSTRAINT fk_owner FOREIGN KEY (owner_id) REFERENCES owner (id)
);