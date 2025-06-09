-- Create Owner table
CREATE TABLE owner
(
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    name            VARCHAR(255) NOT NULL,
    personal_number VARCHAR(255) NOT NULL,
    version         BIGINT DEFAULT 0,
    created_at      timestamp,
    updated_at      timestamp,
    CONSTRAINT uk_owner_personal_number UNIQUE (personal_number)
);

-- Create Car table
CREATE TABLE car
(
    id       BIGINT PRIMARY KEY AUTO_INCREMENT,
    model    VARCHAR(255) NOT NULL,
    owner_id BIGINT,
    CONSTRAINT fk_owner FOREIGN KEY (owner_id) REFERENCES owner (id)
);