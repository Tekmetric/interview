-- Provide SQL scripts here
CREATE TABLE IF NOT EXISTS auto_parts (
    id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    manufacturer VARCHAR(255) NOT NULL,
    quantity INT NOT NULL DEFAULT 0,
    created_date TIMESTAMP NOT NULL,
    last_modified_date TIMESTAMP NOT NULL,
    PRIMARY KEY (id)
);