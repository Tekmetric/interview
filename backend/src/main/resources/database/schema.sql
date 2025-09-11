CREATE TABLE vehicle (
    id VARCHAR(36) PRIMARY KEY,
    created_at DECIMAL(13) NOT NULL,
    updated_at DECIMAL(13) NOT NULL,
    brand VARCHAR(255) NOT NULL,
    model VARCHAR(255) NOT NULL,
    made_year INT NOT NULL,
    color VARCHAR(32) NOT NULL,
    owner_id VARCHAR(36)
);
