INSERT INTO customers (name, created_at, updated_at, version)
VALUES ('Seed Customer', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

INSERT INTO customers (name, created_at, updated_at, version)
VALUES ('Another Customer', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

INSERT INTO work_orders (customer_id, vin, issue_description, status, created_at, updated_at, version)
VALUES (1, '1HGCM82633A004352', 'Initial issue', 'OPEN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);
