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

INSERT INTO users (username, password_hash, role, customer_id, enabled, created_at, updated_at, version)
VALUES (
    'api-admin',
    '$2a$10$9H11gZbYG/bWcdM6nD/Sg.CwbM9Col.hW2bgTdwJ6vi5TzWpd6SOi',
    'ADMIN',
    NULL,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    0
);

INSERT INTO users (username, password_hash, role, customer_id, enabled, created_at, updated_at, version)
VALUES (
    'api-user',
    '$2a$10$XDTdJFJhFPUftTaZ6/41/.5teKaRBqWBWf5mmChaUxgRUUoYD8DBm',
    'USER',
    1,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    0
);
