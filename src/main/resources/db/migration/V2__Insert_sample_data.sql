-- Insert sample warehouses
INSERT INTO warehouses (name, location, is_active) VALUES
('Main Warehouse', 'New York, NY', true),
('Distribution Center', 'Los Angeles, CA', true),
('Regional Hub', 'Chicago, IL', true);

-- Insert sample products
INSERT INTO products (sku, name, description, category, unit, price, is_active, created_at, updated_at) VALUES
('LAPTOP-001', 'Dell Latitude 3520', 'Business laptop with Intel i5 processor', 'Electronics', 'each', 899.99, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('MOUSE-001', 'Logitech MX Master 3', 'Wireless mouse with precision scrolling', 'Electronics', 'each', 99.99, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('CHAIR-001', 'Ergonomic Office Chair', 'Adjustable height office chair with lumbar support', 'Furniture', 'each', 299.99, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('DESK-001', 'Standing Desk 48"', 'Height adjustable standing desk', 'Furniture', 'each', 499.99, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('PEN-001', 'Ballpoint Pen Blue', 'Standard blue ballpoint pen', 'Office Supplies', 'box', 12.99, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('PAPER-001', 'A4 Copy Paper', 'White A4 paper 500 sheets', 'Office Supplies', 'ream', 8.99, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert sample inventory items
INSERT INTO inventory_items (product_id, warehouse_id, quantity_available, quantity_reserved, reorder_point, created_at, updated_at) VALUES
-- Main Warehouse (ID: 1)
(1, 1, 50, 5, 20, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 1, 100, 10, 25, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 1, 25, 2, 10, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 1, 15, 1, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 1, 200, 20, 50, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 1, 500, 50, 100, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Distribution Center (ID: 2)
(1, 2, 30, 3, 15, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 2, 75, 8, 20, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 2, 20, 1, 8, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 2, 150, 15, 40, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 2, 300, 30, 75, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Regional Hub (ID: 3)
(1, 3, 20, 2, 10, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 3, 60, 6, 15, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 3, 10, 0, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 3, 100, 10, 30, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert sample stock movements
INSERT INTO stock_movements (product_id, warehouse_id, movement_type, movement_reason, quantity, unit_cost, reference_number, notes, created_at) VALUES
-- Initial stock purchases
(1, 1, 'IN', 'PURCHASE', 50, 750.00, 'PO-2024-001', 'Initial stock purchase', CURRENT_TIMESTAMP - INTERVAL '30 days'),
(2, 1, 'IN', 'PURCHASE', 100, 85.00, 'PO-2024-002', 'Initial stock purchase', CURRENT_TIMESTAMP - INTERVAL '30 days'),
(3, 1, 'IN', 'PURCHASE', 25, 250.00, 'PO-2024-003', 'Initial stock purchase', CURRENT_TIMESTAMP - INTERVAL '30 days'),

-- Recent sales
(1, 1, 'OUT', 'SALE', 5, 899.99, 'SO-2024-101', 'Sale to corporate client', CURRENT_TIMESTAMP - INTERVAL '5 days'),
(2, 1, 'OUT', 'SALE', 10, 99.99, 'SO-2024-102', 'Bulk sale', CURRENT_TIMESTAMP - INTERVAL '3 days'),
(5, 1, 'OUT', 'SALE', 20, 12.99, 'SO-2024-103', 'Office supply order', CURRENT_TIMESTAMP - INTERVAL '2 days'),

-- Inventory adjustments
(6, 1, 'IN', 'ADJUSTMENT', 50, 8.99, 'ADJ-2024-001', 'Inventory count adjustment', CURRENT_TIMESTAMP - INTERVAL '1 day');