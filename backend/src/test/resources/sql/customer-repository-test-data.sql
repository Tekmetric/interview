-- =============================================================================
-- Test fixture for CustomerRepositoryTest
-- Loaded via @Sql before each test method; rolled back with the test transaction.
-- UUIDs use a simple recognisable pattern (c1/c2/c3 for customers, a1 for app).
-- =============================================================================

INSERT INTO customer (id, first_name, last_name, email, phone, date_of_birth, ssn,
                      address, city, state, zip_code,
                      employment_status, employer_name, annual_income,
                      date_created, date_updated, version)
VALUES
    ('c1000000-0000-7000-8000-000000000000',
     'Jane', 'Doe', 'jane.doe@test.com', '+15555550101', '1985-03-15', '123-45-6789',
     '100 Main St', 'Austin', 'TX', '78701',
     'EMPLOYED', 'Acme Corp', 95000.00,
     NOW(), NOW(), 0),

    ('c2000000-0000-7000-8000-000000000000',
     'John', 'Smith', 'john.smith@test.com', '+15555550102', '1978-07-22', '987-65-4321',
     '200 Oak Ave', 'Dallas', 'TX', '75201',
     'SELF_EMPLOYED', 'Smith Consulting LLC', 120000.00,
     NOW(), NOW(), 0),

    ('c3000000-0000-7000-8000-000000000000',
     'Maria', 'Garcia', 'maria.garcia@test.com', '+15555550103', '1992-11-08', '456-78-9012',
     '300 Elm St', 'Houston', 'TX', '77001',
     'EMPLOYED', 'Texas Health Systems', 72000.00,
     NOW(), NOW(), 0);

INSERT INTO credit_application (id, customer_id, status, requested_loan_amount, loan_purpose,
                                monthly_debt, notes, submitted_at,
                                date_created, date_updated, version)
VALUES
    ('a1000000-0000-7000-8000-000000000000',
     'c1000000-0000-7000-8000-000000000000',
     'SUBMITTED', 35000.00, 'VEHICLE_PURCHASE', 500.00,
     'Seeded for cascade-delete assertion',
     NOW(), NOW(), NOW(), 0);
