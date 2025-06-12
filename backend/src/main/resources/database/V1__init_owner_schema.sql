-- Create Owner table
CREATE TABLE owner
(
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    name            VARCHAR(255) NOT NULL,
    personal_number VARCHAR(255) NOT NULL,
    birth_date      timestamp    NOT NULL,
    address         VARCHAR(500) NOT NULL,
    version         BIGINT DEFAULT 0,
    created_at      timestamp,
    updated_at      timestamp,
    CONSTRAINT uk_owner_personal_number UNIQUE (personal_number)
);