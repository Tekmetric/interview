INSERT INTO customers (name, created_at, updated_at, version)
VALUES ('Seed Customer', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

INSERT INTO customers (name, created_at, updated_at, version)
VALUES ('Another Customer', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

INSERT INTO work_orders (customer_id, vin, issue_description, status, created_at, updated_at, version)
VALUES (1, '1HGCM82633A004352', 'Initial issue', 'OPEN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

INSERT INTO work_orders (customer_id, vin, issue_description, status, created_at, updated_at, version)
VALUES (1, 'JH4KA9650MC012345', 'Waiting for spare parts', 'IN_PROGRESS', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

INSERT INTO work_orders (customer_id, vin, issue_description, status, created_at, updated_at, version)
VALUES (1, '2HGFC2F59KH012346', 'Completed maintenance', 'COMPLETED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

INSERT INTO work_orders (customer_id, vin, issue_description, status, created_at, updated_at, version)
VALUES (2, '3C6JR6AT1DG123456', 'Other customer order', 'OPEN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);
