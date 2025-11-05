-- Insert sample ingredients for a commercial kitchen
INSERT INTO ingredients (name, category, quantity, unit, minimum_stock, price_per_unit, supplier, expiration_date, requires_refrigeration, last_updated) VALUES
('Olive Oil', 'Oils', 50.0, 'liters', 10.0, 8.50, 'Mediterranean Foods Co', '2025-12-31', false, CURRENT_TIMESTAMP()),
('All-Purpose Flour', 'Grains', 100.0, 'kg', 20.0, 1.20, 'Grain Suppliers Inc', '2025-06-30', false, CURRENT_TIMESTAMP()),
('Fresh Tomatoes', 'Vegetables', 25.0, 'kg', 15.0, 3.50, 'Local Farm Direct', '2024-11-15', true, CURRENT_TIMESTAMP()),
('Chicken Breast', 'Proteins', 30.0, 'kg', 10.0, 12.00, 'Premium Poultry', '2024-11-10', true, CURRENT_TIMESTAMP()),
('Mozzarella Cheese', 'Dairy', 15.0, 'kg', 5.0, 9.75, 'Dairy Delight', '2024-11-20', true, CURRENT_TIMESTAMP()),
('Basil', 'Herbs', 2.0, 'kg', 1.0, 15.00, 'Fresh Herbs Co', '2024-11-08', true, CURRENT_TIMESTAMP()),
('Garlic', 'Vegetables', 8.0, 'kg', 3.0, 4.25, 'Local Farm Direct', '2025-01-15', false, CURRENT_TIMESTAMP()),
('Heavy Cream', 'Dairy', 20.0, 'liters', 8.0, 5.50, 'Dairy Delight', '2024-11-12', true, CURRENT_TIMESTAMP()),
('Pasta (Penne)', 'Grains', 40.0, 'kg', 15.0, 2.80, 'Italian Imports', '2026-03-31', false, CURRENT_TIMESTAMP()),
('Sea Salt', 'Seasonings', 10.0, 'kg', 3.0, 2.00, 'Spice Traders', '2027-12-31', false, CURRENT_TIMESTAMP()),
('Black Pepper', 'Seasonings', 3.0, 'kg', 1.0, 18.00, 'Spice Traders', '2026-06-30', false, CURRENT_TIMESTAMP()),
('Butter', 'Dairy', 12.0, 'kg', 5.0, 7.50, 'Dairy Delight', '2024-11-25', true, CURRENT_TIMESTAMP()),
('Eggs', 'Proteins', 500.0, 'units', 100.0, 0.25, 'Local Farm Direct', '2024-11-14', true, CURRENT_TIMESTAMP()),
('White Onions', 'Vegetables', 20.0, 'kg', 8.0, 2.75, 'Vegetable Wholesale', '2024-12-01', false, CURRENT_TIMESTAMP()),
('Bell Peppers', 'Vegetables', 10.0, 'kg', 5.0, 4.50, 'Vegetable Wholesale', '2024-11-12', true, CURRENT_TIMESTAMP());