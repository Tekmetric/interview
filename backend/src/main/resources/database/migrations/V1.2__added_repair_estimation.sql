ALTER TABLE repair_order
ADD COLUMN estimation_pdf_object_key VARCHAR(255) DEFAULT NULL;

ALTER TABLE repair_order
ADD COLUMN estimation_status VARCHAR(255) DEFAULT NULL;
