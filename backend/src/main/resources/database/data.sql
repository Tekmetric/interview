-- DDL visualized here for reference; actual table creation is handled by JPA/Hibernate
/*CREATE TABLE IF NOT EXISTS repair_order (
    id IDENTITY PRIMARY KEY,
    version BIGINT NOT NULL,
    order_number VARCHAR(32) NOT NULL UNIQUE,
    vin VARCHAR(17) NOT NULL,
    vehicle_year INT,
    vehicle_make VARCHAR(64),
    vehicle_model VARCHAR(64),
    customer_name VARCHAR(128),
    customer_phone VARCHAR(32),
    status VARCHAR(16) NOT NULL,
    total DECIMAL(19,2) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
    );

CREATE INDEX IF NOT EXISTS idx_order_number ON repair_order(order_number);
CREATE INDEX IF NOT EXISTS idx_status ON repair_order(status);
CREATE INDEX IF NOT EXISTS idx_vin ON repair_order(vin);
CREATE INDEX IF NOT EXISTS idx_created_at ON repair_order(created_at);

CREATE TABLE IF NOT EXISTS repair_line_item (
    id IDENTITY PRIMARY KEY,
    repair_order_id BIGINT NOT NULL,
    description VARCHAR(256) NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(19,2) NOT NULL,
    line_total DECIMAL(19,2) NOT NULL,
    CONSTRAINT fk_repair_order FOREIGN KEY (repair_order_id)
    REFERENCES repair_order(id) ON DELETE CASCADE
    );
 */


-- Initialize with 20 repair orders with line items for testing

INSERT INTO repair_order (version, order_number, vin, vehicle_year, vehicle_make, vehicle_model, customer_name, customer_phone, status, total, created_at, updated_at) VALUES (0, 'RO-2000', '1HGCM82633A004352', 2010, 'Honda', 'Civic', 'Customer 1', '+1-555-3000', 'OPEN', 0.00, TIMESTAMP '2025-06-01 09:00:00', TIMESTAMP '2025-06-01 09:00:00');

INSERT INTO repair_line_item (repair_order_id, description, quantity, unit_price, line_total) SELECT id, 'Oil change', 1, 59.99, 59.99 FROM repair_order WHERE order_number='RO-2000';

INSERT INTO repair_line_item (repair_order_id, description, quantity, unit_price, line_total) SELECT id, 'Air filter', 1, 24.50, 24.50 FROM repair_order WHERE order_number='RO-2000';

INSERT INTO repair_order (version, order_number, vin, vehicle_year, vehicle_make, vehicle_model, customer_name, customer_phone, status, total, created_at, updated_at) VALUES (0, 'RO-2001', '2C4RC1BG4HR999999', 2011, 'Honda', 'Accord', 'Customer 2', '+1-555-3001', 'IN_PROGRESS', 0.00, TIMESTAMP '2025-06-02 09:00:00', TIMESTAMP '2025-06-02 09:00:00');

INSERT INTO repair_line_item (repair_order_id, description, quantity, unit_price, line_total) SELECT id, 'Brake pads replacement', 1, 199.99, 199.99 FROM repair_order WHERE order_number='RO-2001';

INSERT INTO repair_order (version, order_number, vin, vehicle_year, vehicle_make, vehicle_model, customer_name, customer_phone, status, total, created_at, updated_at) VALUES (0, 'RO-2002', '1N4AL11D75C123456', 2012, 'Toyota', 'Camry', 'Customer 3', '+1-555-3002', 'COMPLETED', 0.00, TIMESTAMP '2025-06-03 09:00:00', TIMESTAMP '2025-06-03 09:00:00');

INSERT INTO repair_line_item (repair_order_id, description, quantity, unit_price, line_total) SELECT id, 'Wheel alignment', 1, 89.00, 89.00 FROM repair_order WHERE order_number='RO-2002';

INSERT INTO repair_line_item (repair_order_id, description, quantity, unit_price, line_total) SELECT id, 'Coolant flush', 1, 129.00, 129.00 FROM repair_order WHERE order_number='RO-2002';

INSERT INTO repair_order (version, order_number, vin, vehicle_year, vehicle_make, vehicle_model, customer_name, customer_phone, status, total, created_at, updated_at) VALUES (0, 'RO-2003', '1FTFW1EF1EFA00001', 2013, 'Toyota', 'Corolla', 'Customer 4', '+1-555-3003', 'CANCELED', 0.00, TIMESTAMP '2025-06-04 09:00:00', TIMESTAMP '2025-06-04 09:00:00');

INSERT INTO repair_line_item (repair_order_id, description, quantity, unit_price, line_total) SELECT id, 'Battery replacement', 1, 159.00, 159.00 FROM repair_order WHERE order_number='RO-2003';

INSERT INTO repair_order (version, order_number, vin, vehicle_year, vehicle_make, vehicle_model, customer_name, customer_phone, status, total, created_at, updated_at) VALUES (0, 'RO-2004', '1GCHK23U83F123456', 2014, 'Ford', 'F-150', 'Customer 5', '+1-555-3004', 'OPEN', 0.00, TIMESTAMP '2025-06-05 09:00:00', TIMESTAMP '2025-06-05 09:00:00');

INSERT INTO repair_line_item (repair_order_id, description, quantity, unit_price, line_total) SELECT id, 'Oil change', 1, 59.99, 59.99 FROM repair_order WHERE order_number='RO-2004';

INSERT INTO repair_line_item (repair_order_id, description, quantity, unit_price, line_total) SELECT id, 'Air filter', 1, 24.50, 24.50 FROM repair_order WHERE order_number='RO-2004';

INSERT INTO repair_order (version, order_number, vin, vehicle_year, vehicle_make, vehicle_model, customer_name, customer_phone, status, total, created_at, updated_at) VALUES (0, 'RO-2005', 'JN8AS5MT9CW001234', 2015, 'Ford', 'Escape', 'Customer 6', '+1-555-3005', 'IN_PROGRESS', 0.00, TIMESTAMP '2025-06-06 09:00:00', TIMESTAMP '2025-06-06 09:00:00');

INSERT INTO repair_line_item (repair_order_id, description, quantity, unit_price, line_total) SELECT id, 'Brake pads replacement', 1, 199.99, 199.99 FROM repair_order WHERE order_number='RO-2005';

INSERT INTO repair_order (version, order_number, vin, vehicle_year, vehicle_make, vehicle_model, customer_name, customer_phone, status, total, created_at, updated_at) VALUES (0, 'RO-2006', 'WBA3B3C52DF123456', 2016, 'Chevrolet', 'Silverado', 'Customer 7', '+1-555-3006', 'COMPLETED', 0.00, TIMESTAMP '2025-06-07 09:00:00', TIMESTAMP '2025-06-07 09:00:00');

INSERT INTO repair_line_item (repair_order_id, description, quantity, unit_price, line_total) SELECT id, 'Wheel alignment', 1, 89.00, 89.00 FROM repair_order WHERE order_number='RO-2006';

INSERT INTO repair_line_item (repair_order_id, description, quantity, unit_price, line_total) SELECT id, 'Coolant flush', 1, 129.00, 129.00 FROM repair_order WHERE order_number='RO-2006';

INSERT INTO repair_order (version, order_number, vin, vehicle_year, vehicle_make, vehicle_model, customer_name, customer_phone, status, total, created_at, updated_at) VALUES (0, 'RO-2007', 'WDCGG8HB8BF123456', 2017, 'Chevrolet', 'Malibu', 'Customer 8', '+1-555-3007', 'CANCELED', 0.00, TIMESTAMP '2025-06-08 09:00:00', TIMESTAMP '2025-06-08 09:00:00');

INSERT INTO repair_line_item (repair_order_id, description, quantity, unit_price, line_total) SELECT id, 'Battery replacement', 1, 159.00, 159.00 FROM repair_order WHERE order_number='RO-2007';

INSERT INTO repair_order (version, order_number, vin, vehicle_year, vehicle_make, vehicle_model, customer_name, customer_phone, status, total, created_at, updated_at) VALUES (0, 'RO-2008', 'JHMCM56557C123456', 2018, 'Nissan', 'Altima', 'Customer 9', '+1-555-3008', 'OPEN', 0.00, TIMESTAMP '2025-06-09 09:00:00', TIMESTAMP '2025-06-09 09:00:00');

INSERT INTO repair_line_item (repair_order_id, description, quantity, unit_price, line_total) SELECT id, 'Oil change', 1, 59.99, 59.99 FROM repair_order WHERE order_number='RO-2008';

INSERT INTO repair_line_item (repair_order_id, description, quantity, unit_price, line_total) SELECT id, 'Air filter', 1, 24.50, 24.50 FROM repair_order WHERE order_number='RO-2008';

INSERT INTO repair_order (version, order_number, vin, vehicle_year, vehicle_make, vehicle_model, customer_name, customer_phone, status, total, created_at, updated_at) VALUES (0, 'RO-2009', 'JTDBE32K820123456', 2019, 'Nissan', 'Rogue', 'Customer 10', '+1-555-3009', 'IN_PROGRESS', 0.00, TIMESTAMP '2025-06-10 09:00:00', TIMESTAMP '2025-06-10 09:00:00');

INSERT INTO repair_line_item (repair_order_id, description, quantity, unit_price, line_total) SELECT id, 'Brake pads replacement', 1, 199.99, 199.99 FROM repair_order WHERE order_number='RO-2009';

INSERT INTO repair_order (version, order_number, vin, vehicle_year, vehicle_make, vehicle_model, customer_name, customer_phone, status, total, created_at, updated_at) VALUES (0, 'RO-2010', '1FAFP404XWF123456', 2020, 'BMW', '3 Series', 'Customer 11', '+1-555-3010', 'COMPLETED', 0.00, TIMESTAMP '2025-06-11 09:00:00', TIMESTAMP '2025-06-11 09:00:00');

INSERT INTO repair_line_item (repair_order_id, description, quantity, unit_price, line_total) SELECT id, 'Wheel alignment', 1, 89.00, 89.00 FROM repair_order WHERE order_number='RO-2010';

INSERT INTO repair_line_item (repair_order_id, description, quantity, unit_price, line_total) SELECT id, 'Coolant flush', 1, 129.00, 129.00 FROM repair_order WHERE order_number='RO-2010';

INSERT INTO repair_order (version, order_number, vin, vehicle_year, vehicle_make, vehicle_model, customer_name, customer_phone, status, total, created_at, updated_at) VALUES (0, 'RO-2011', '1G1ZE5ST0GF123456', 2021, 'BMW', 'X3', 'Customer 12', '+1-555-3011', 'CANCELED', 0.00, TIMESTAMP '2025-06-12 09:00:00', TIMESTAMP '2025-06-12 09:00:00');

INSERT INTO repair_line_item (repair_order_id, description, quantity, unit_price, line_total) SELECT id, 'Battery replacement', 1, 159.00, 159.00 FROM repair_order WHERE order_number='RO-2011';

INSERT INTO repair_order (version, order_number, vin, vehicle_year, vehicle_make, vehicle_model, customer_name, customer_phone, status, total, created_at, updated_at) VALUES (0, 'RO-2012', '3N1AB7AP4FY123456', 2022, 'Mercedes', 'C-Class', 'Customer 13', '+1-555-3012', 'OPEN', 0.00, TIMESTAMP '2025-06-13 09:00:00', TIMESTAMP '2025-06-13 09:00:00');

INSERT INTO repair_line_item (repair_order_id, description, quantity, unit_price, line_total) SELECT id, 'Oil change', 1, 59.99, 59.99 FROM repair_order WHERE order_number='RO-2012';

INSERT INTO repair_line_item (repair_order_id, description, quantity, unit_price, line_total) SELECT id, 'Air filter', 1, 24.50, 24.50 FROM repair_order WHERE order_number='RO-2012';

INSERT INTO repair_order (version, order_number, vin, vehicle_year, vehicle_make, vehicle_model, customer_name, customer_phone, status, total, created_at, updated_at) VALUES (0, 'RO-2013', 'WBAZX1C51LF123456', 2023, 'Mercedes', 'GLC', 'Customer 14', '+1-555-3013', 'IN_PROGRESS', 0.00, TIMESTAMP '2025-06-14 09:00:00', TIMESTAMP '2025-06-14 09:00:00');

INSERT INTO repair_line_item (repair_order_id, description, quantity, unit_price, line_total) SELECT id, 'Brake pads replacement', 1, 199.99, 199.99 FROM repair_order WHERE order_number='RO-2013';

INSERT INTO repair_order (version, order_number, vin, vehicle_year, vehicle_make, vehicle_model, customer_name, customer_phone, status, total, created_at, updated_at) VALUES (0, 'RO-2014', '1HGAF5645HA123456', 2024, 'Honda', 'CR-V', 'Customer 15', '+1-555-3014', 'COMPLETED', 0.00, TIMESTAMP '2025-06-15 09:00:00', TIMESTAMP '2025-06-15 09:00:00');

INSERT INTO repair_line_item (repair_order_id, description, quantity, unit_price, line_total) SELECT id, 'Wheel alignment', 1, 89.00, 89.00 FROM repair_order WHERE order_number='RO-2014';

INSERT INTO repair_line_item (repair_order_id, description, quantity, unit_price, line_total) SELECT id, 'Coolant flush', 1, 129.00, 129.00 FROM repair_order WHERE order_number='RO-2014';

INSERT INTO repair_order (version, order_number, vin, vehicle_year, vehicle_make, vehicle_model, customer_name, customer_phone, status, total, created_at, updated_at) VALUES (0, 'RO-2015', 'JTMZF33V605123456', 2010, 'Toyota', 'RAV4', 'Customer 16', '+1-555-3015', 'CANCELED', 0.00, TIMESTAMP '2025-06-16 09:00:00', TIMESTAMP '2025-06-16 09:00:00');

INSERT INTO repair_line_item (repair_order_id, description, quantity, unit_price, line_total) SELECT id, 'Battery replacement', 1, 159.00, 159.00 FROM repair_order WHERE order_number='RO-2015';

INSERT INTO repair_order (version, order_number, vin, vehicle_year, vehicle_make, vehicle_model, customer_name, customer_phone, status, total, created_at, updated_at) VALUES (0, 'RO-2016', '3FA6P0H74HR123456', 2011, 'Ford', 'Fusion', 'Customer 17', '+1-555-3016', 'OPEN', 0.00, TIMESTAMP '2025-06-17 09:00:00', TIMESTAMP '2025-06-17 09:00:00');

INSERT INTO repair_line_item (repair_order_id, description, quantity, unit_price, line_total) SELECT id, 'Oil change', 1, 59.99, 59.99 FROM repair_order WHERE order_number='RO-2016';

INSERT INTO repair_line_item (repair_order_id, description, quantity, unit_price, line_total) SELECT id, 'Air filter', 1, 24.50, 24.50 FROM repair_order WHERE order_number='RO-2016';

INSERT INTO repair_order (version, order_number, vin, vehicle_year, vehicle_make, vehicle_model, customer_name, customer_phone, status, total, created_at, updated_at) VALUES (0, 'RO-2017', '2GNALDEK9D1234567', 2012, 'Chevrolet', 'Equinox', 'Customer 18', '+1-555-3017', 'IN_PROGRESS', 0.00, TIMESTAMP '2025-06-18 09:00:00', TIMESTAMP '2025-06-18 09:00:00');

INSERT INTO repair_line_item (repair_order_id, description, quantity, unit_price, line_total) SELECT id, 'Brake pads replacement', 1, 199.99, 199.99 FROM repair_order WHERE order_number='RO-2017';

INSERT INTO repair_order (version, order_number, vin, vehicle_year, vehicle_make, vehicle_model, customer_name, customer_phone, status, total, created_at, updated_at) VALUES (0, 'RO-2018', '3N1CB51D76L123456', 2013, 'Nissan', 'Sentra', 'Customer 19', '+1-555-3018', 'COMPLETED', 0.00, TIMESTAMP '2025-06-19 09:00:00', TIMESTAMP '2025-06-19 09:00:00');

INSERT INTO repair_line_item (repair_order_id, description, quantity, unit_price, line_total) SELECT id, 'Wheel alignment', 1, 89.00, 89.00 FROM repair_order WHERE order_number='RO-2018';

INSERT INTO repair_line_item (repair_order_id, description, quantity, unit_price, line_total) SELECT id, 'Coolant flush', 1, 129.00, 129.00 FROM repair_order WHERE order_number='RO-2018';

INSERT INTO repair_order (version, order_number, vin, vehicle_year, vehicle_make, vehicle_model, customer_name, customer_phone, status, total, created_at, updated_at) VALUES (0, 'RO-2019', 'WBAFR7C50BC123456', 2014, 'BMW', '5 Series', 'Customer 20', '+1-555-3019', 'CANCELED', 0.00, TIMESTAMP '2025-06-20 09:00:00', TIMESTAMP '2025-06-20 09:00:00');

INSERT INTO repair_line_item (repair_order_id, description, quantity, unit_price, line_total) SELECT id, 'Battery replacement', 1, 159.00, 159.00 FROM repair_order WHERE order_number='RO-2019';


UPDATE repair_order SET total = (SELECT COALESCE(SUM(line_total),0) FROM repair_line_item WHERE repair_order_id = repair_order.id);