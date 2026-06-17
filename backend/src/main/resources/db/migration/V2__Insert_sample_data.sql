-- V2__Insert_sample_data.sql
-- Insert sample data for development and testing

-- Insert sample customers
INSERT INTO customers (first_name, last_name, email, phone, created_date, updated_date) VALUES
    ('John', 'Doe', 'john.doe@example.com', '+1-555-0101', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Jane', 'Smith', 'jane.smith@example.com', '+1-555-0102', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Bob', 'Johnson', 'bob.johnson@example.com', '+1-555-0103', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert sample customer profiles
INSERT INTO customer_profiles (customer_id, address, date_of_birth, preferred_contact_method) VALUES
    (1, '123 Main St, Anytown, ST 12345', '1985-03-15', 'EMAIL'),
    (2, '456 Oak Ave, Another City, ST 67890', '1990-07-22', 'PHONE'),
    (3, '789 Pine Rd, Some Place, ST 54321', '1988-11-08', 'SMS');