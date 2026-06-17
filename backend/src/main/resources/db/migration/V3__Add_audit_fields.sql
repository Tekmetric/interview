-- V3__Add_audit_fields.sql
-- Add audit fields (created_by, updated_by) to customers table

-- Add audit fields to customers table only (H2 requires separate ALTER statements)
ALTER TABLE customers ADD COLUMN created_by VARCHAR(100);
ALTER TABLE customers ADD COLUMN updated_by VARCHAR(100);

-- Update existing records to have default audit values
-- Set existing records as created/updated by 'SYSTEM' since we don't know the original user
UPDATE customers
SET created_by = 'SYSTEM',
    updated_by = 'SYSTEM'
WHERE created_by IS NULL;