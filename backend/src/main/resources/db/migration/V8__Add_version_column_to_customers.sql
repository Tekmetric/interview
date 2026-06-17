-- V8__Add_version_column_to_customers.sql
-- Add version column for optimistic locking to customers table

-- Add version column to customers table
ALTER TABLE customers ADD COLUMN version BIGINT NOT NULL DEFAULT 0;