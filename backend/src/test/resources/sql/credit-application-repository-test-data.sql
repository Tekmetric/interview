-- =============================================================================
-- Test fixture for CreditApplicationRepositoryTest
-- Loaded via @Sql before each test method; rolled back with the test transaction.
-- =============================================================================

INSERT INTO customer (id, first_name, last_name, email, phone, date_of_birth, ssn,
                      address, city, state, zip_code,
                      employment_status, employer_name, annual_income,
                      date_created, date_updated, version)
VALUES
    ('c1000000-0000-7000-8000-000000000000',
     'Jane', 'Doe', 'jane.doe@test.com', '+15555550101', '1985-03-15', '123-45-6789',
     '100 Main St', 'Austin', 'TX', '78701',
     'EMPLOYED', 'Acme Corp', 100000.00,
     NOW(), NOW(), 0);

INSERT INTO credit_application (id, customer_id, status, requested_loan_amount, loan_purpose,
                                monthly_debt, notes, submitted_at,
                                date_created, date_updated, version)
VALUES
    ('a1000000-0000-7000-8000-000000000000',
     'c1000000-0000-7000-8000-000000000000',
     'SUBMITTED', 30000.00, 'VEHICLE_PURCHASE', 400.00,
     NULL, NOW(), NOW(), NOW(), 0),

    ('a2000000-0000-7000-8000-000000000000',
     'c1000000-0000-7000-8000-000000000000',
     'APPROVED', 30000.00, 'VEHICLE_PURCHASE', 400.00,
     NULL, NOW(), NOW(), NOW(), 0);
