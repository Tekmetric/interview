-- Command Database Schema
CREATE TABLE IF NOT EXISTS widgets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255)
);

-- Spring Modulith Event Publication table
CREATE TABLE IF NOT EXISTS event_publication (
    id VARCHAR(36) PRIMARY KEY,
    listener_id VARCHAR(512) NOT NULL,
    event_type VARCHAR(512) NOT NULL,
    serialized_event VARCHAR(4000) NOT NULL,
    publication_date TIMESTAMP NOT NULL,
    completion_date TIMESTAMP
);
