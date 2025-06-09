ALTER TABLE customer ADD COLUMN birth_year SMALLINT NULL;

-- Create an index on the birth_year column for access (for example if birth_year is a field likely to be sorted frequently in retrieve all operations)
CREATE INDEX birth_year_idx ON customer (birth_year);