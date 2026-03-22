-- =============================================================================
-- Seed Data: Customer Financing & Credit Application Service
-- Executed via spring.sql.init.data-locations after schema.sql.
-- H2 resets on every restart — these rows exist for local development only.
-- =============================================================================

-- -----------------------------------------------------------------------------
-- Customers
-- -----------------------------------------------------------------------------
INSERT INTO customer (id, first_name, last_name, email, phone, date_of_birth, ssn,
                      address, city, state, zip_code,
                      employment_status, employer_name, annual_income,
                      date_created, date_updated, version)
VALUES
    ('018fae10-0000-7000-8000-000000000001',
     'Jane', 'Doe', 'jane.doe@example.com', '+15555550101', '1985-03-15', '123-45-6789',
     '100 Main St', 'Austin', 'TX', '78701',
     'EMPLOYED', 'Acme Corp', 95000.00,
     NOW(), NOW(), 0),

    ('018fae10-0000-7000-8000-000000000002',
     'John', 'Smith', 'john.smith@example.com', '+15555550102', '1978-07-22', '987-65-4321',
     '200 Oak Ave', 'Dallas', 'TX', '75201',
     'SELF_EMPLOYED', 'Smith Consulting LLC', 120000.00,
     NOW(), NOW(), 0),

    ('018fae10-0000-7000-8000-000000000003',
     'Maria', 'Garcia', 'maria.garcia@example.com', '+15555550103', '1992-11-08', '456-78-9012',
     '300 Elm St', 'Houston', 'TX', '77001',
     'EMPLOYED', 'Texas Health Systems', 72000.00,
     NOW(), NOW(), 0);

-- -----------------------------------------------------------------------------
-- Credit Applications
-- -----------------------------------------------------------------------------
INSERT INTO credit_application (id, customer_id, status, requested_loan_amount, loan_purpose,
                                monthly_debt, notes, submitted_at,
                                date_created, date_updated, version)
VALUES
    ('018fae20-0000-7000-8000-000000000001',
     '018fae10-0000-7000-8000-000000000001',
     'SUBMITTED', 35000.00, 'VEHICLE_PURCHASE', 500.00,
     'First-time buyer, stable employment for 5 years.',
     NOW(), NOW(), NOW(), 0),

    ('018fae20-0000-7000-8000-000000000002',
     '018fae10-0000-7000-8000-000000000002',
     'UNDER_REVIEW', 55000.00, 'REFINANCE', 1200.00,
     'Refinancing existing vehicle loan for better rate.',
     NOW(), NOW(), NOW(), 0),

    ('018fae20-0000-7000-8000-000000000003',
     '018fae10-0000-7000-8000-000000000003',
     'APPROVED', 28000.00, 'VEHICLE_PURCHASE', 300.00,
     'Pre-approved through dealer programme.',
     NOW(), NOW(), NOW(), 0);

-- -----------------------------------------------------------------------------
-- Supporting Documents
-- Object keys follow the canonical pattern: customers/{id}/applications/{id}/documents/{type}
-- -----------------------------------------------------------------------------
INSERT INTO supporting_document (id, application_id, document_type, object_key, file_name,
                                 date_created, date_updated, version)
VALUES
    -- Jane Doe — SUBMITTED application
    ('018fae30-0000-7000-8000-000000000001',
     '018fae20-0000-7000-8000-000000000001',
     'PROOF_OF_INCOME',
     'customers/018fae10-0000-7000-8000-000000000001/applications/018fae20-0000-7000-8000-000000000001/documents/proof_of_income',
     'paystub_jan_2024.pdf',
     NOW(), NOW(), 0),

    ('018fae30-0000-7000-8000-000000000002',
     '018fae20-0000-7000-8000-000000000001',
     'GOVERNMENT_ID',
     'customers/018fae10-0000-7000-8000-000000000001/applications/018fae20-0000-7000-8000-000000000001/documents/government_id',
     'drivers_license.jpg',
     NOW(), NOW(), 0),

    -- John Smith — UNDER_REVIEW application
    ('018fae30-0000-7000-8000-000000000003',
     '018fae20-0000-7000-8000-000000000002',
     'PROOF_OF_INCOME',
     'customers/018fae10-0000-7000-8000-000000000002/applications/018fae20-0000-7000-8000-000000000002/documents/proof_of_income',
     'bank_statement_q4.pdf',
     NOW(), NOW(), 0),

    ('018fae30-0000-7000-8000-000000000004',
     '018fae20-0000-7000-8000-000000000002',
     'TAX_RETURN',
     'customers/018fae10-0000-7000-8000-000000000002/applications/018fae20-0000-7000-8000-000000000002/documents/tax_return',
     'tax_return_2023.pdf',
     NOW(), NOW(), 0),

    -- Maria Garcia — APPROVED application
    ('018fae30-0000-7000-8000-000000000005',
     '018fae20-0000-7000-8000-000000000003',
     'PROOF_OF_INCOME',
     'customers/018fae10-0000-7000-8000-000000000003/applications/018fae20-0000-7000-8000-000000000003/documents/proof_of_income',
     'employment_letter.pdf',
     NOW(), NOW(), 0);
