-- Create running_event table
CREATE TABLE running_event (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    date_time BIGINT NOT NULL,
    location VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    further_information VARCHAR(1000)
);

-- Create index on date_time column for efficient querying by date
CREATE INDEX idx_running_event_date_time ON running_event(date_time);