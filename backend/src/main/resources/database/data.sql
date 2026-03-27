-- Provide SQL scripts here
CREATE TABLE IF NOT EXISTS contact (
    id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    middle_name VARCHAR(255),
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(11) NOT NULL,
    phone_type VARCHAR(6)
);

INSERT INTO contact (id, first_name, middle_name, last_name, email, phone, phone_type)
VALUES
    ('550e8400-e29b-41d4-a716-446655440000', 'John', 'Michael', 'Doe', 'john.doe@example.com', '1234567890', 'MOBILE'),
    ('6ba7b810-9dad-11d1-80b4-00c04fd430c8', 'Jane', NULL, 'Smith', 'jane.smith@example.com', '9876543210', 'HOME'),
    ('f47ac10b-58cc-4372-a567-0e02b2c3d479', 'Bob', 'Lee', 'Johnson', 'bob.j@example.com', '5551234567', 'WORK');
