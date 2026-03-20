INSERT INTO repair_orders (customer_name, description, status, created_at, updated_at)
VALUES
('Acme Corp', 'Initial assessment', 'OPEN', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
('Globex', 'Replace worn brake pads', 'IN_PROGRESS', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());