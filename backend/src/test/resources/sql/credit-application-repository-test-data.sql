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

INSERT INTO supporting_document (id, application_id, document_type, object_key, file_name,
                                 date_created, date_updated, version)
VALUES
    ('d1000000-0000-7000-8000-000000000000',
     'a1000000-0000-7000-8000-000000000000',
     'PROOF_OF_INCOME',
     'customers/c1000000-0000-7000-8000-000000000000/applications/a1000000-0000-7000-8000-000000000000/documents/proof_of_income',
     'income.pdf', NOW(), NOW(), 0),

    ('d2000000-0000-7000-8000-000000000000',
     'a1000000-0000-7000-8000-000000000000',
     'GOVERNMENT_ID',
     'customers/c1000000-0000-7000-8000-000000000000/applications/a1000000-0000-7000-8000-000000000000/documents/government_id',
     'license.pdf', NOW(), NOW(), 0),

    ('d3000000-0000-7000-8000-000000000000',
     'a2000000-0000-7000-8000-000000000000',
     'TAX_RETURN',
     'customers/c1000000-0000-7000-8000-000000000000/applications/a2000000-0000-7000-8000-000000000000/documents/tax_return',
     'taxes.pdf', NOW(), NOW(), 0);
