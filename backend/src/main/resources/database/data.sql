-- Drop table if exists to ensure clean state
DROP TABLE IF EXISTS running_event;

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

-- Insert sample data
-- Current date/time in milliseconds
INSERT INTO running_event (name, date_time, location, description, further_information)
VALUES (
    'Spring Marathon 2025',
    1743484800000, -- April 29, 2025, 10:00 AM
    'Central Park, New York',
    'Annual spring marathon through the scenic Central Park. Open to runners of all levels.',
    'Water stations every 2 miles. Registration closes 2 weeks before the event.'
);

INSERT INTO running_event (name, date_time, location, description, further_information)
VALUES (
    'Summer 5K Charity Run',
    1751313600000, -- July 31, 2025, 9:00 AM
    'Riverside Park, Chicago',
    'A charity 5K run to raise funds for local children''s hospitals. Family-friendly event.',
    'Post-run celebration with food and music. Donations welcome.'
);

INSERT INTO running_event (name, date_time, location, description, further_information)
VALUES (
    'Autumn Trail Half Marathon',
    1759608000000, -- October 4, 2025, 8:00 AM
    'Redwood Forest Trail, San Francisco',
    'Challenging half marathon through beautiful autumn forest trails. Experienced runners recommended.',
    'Trail running shoes required. Limited to 500 participants.'
);