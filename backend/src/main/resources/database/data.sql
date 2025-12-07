-- Provide SQL scripts here
CREATE SCHEMA IF NOT EXISTS shop_management AUTHORIZATION sa;
SET SCHEMA SHOP_MANAGEMENT;

CREATE TABLE IF NOT EXISTS purchase_orders (purchase_order_id bigint primary key, supplier_name varchar(50) not null,
    placed_on date, expected_delivery date, actual_delivery date, total_cost decimal(10,2) not null, total_weight decimal(8,2),
    purchase_order_status varchar(20) not null);

CREATE SEQUENCE IF NOT EXISTS purchase_order_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS purchase_order_lines (purchase_order_line_id bigint primary key, purchase_order_id bigint not null,
    sku varchar(12) not null, description varchar(100), color varchar(50), quantity smallint not null, unit_cost decimal(10,2), unit_weight decimal(8,2));

ALTER TABLE purchase_order_lines ADD FOREIGN KEY (purchase_order_id) REFERENCES purchase_orders(purchase_order_id);

CREATE SEQUENCE IF NOT EXISTS purchase_order_line_id_seq START WITH 1 INCREMENT BY 1;

INSERT INTO purchase_orders (purchase_order_id, supplier_name, placed_on, expected_delivery, actual_delivery,
                             total_cost, total_weight, purchase_order_status)
VALUES ( NEXT VALUE FOR purchase_order_id_seq, 'TOYOTA OEM PARTS', '2025-11-15', '2025-12-3', '2025-12-3', 2571.23, 80.25, 'DELIVERED' ),
       ( NEXT VALUE FOR purchase_order_id_seq, 'Car Parts International', '2025-11-23', '2025-12-15', null, 578.67, 154.50, 'TRANSIT' ),
       ( NEXT VALUE FOR purchase_order_id_seq, 'USA Parts Supplier', '2025-12-5', '2025-12-20', null, 12547.00, 1241.75, 'CONFIRMED' );

INSERT INTO purchase_order_lines (purchase_order_line_id, purchase_order_id, sku, description, color, quantity, unit_cost, unit_weight)
VALUES (NEXT VALUE FOR purchase_order_line_id_seq, (SELECT purchase_orders.purchase_order_id from purchase_orders where supplier_name='TOYOTA OEM PARTS'), '546349598745', '2023 Toyota 4Runner Radio', 'black', 3, 857.08, 26.75),
       (NEXT VALUE FOR purchase_order_line_id_seq, (SELECT purchase_orders.purchase_order_id from purchase_orders where supplier_name='Car Parts International'), '587412365985', 'Headlight Repair Kit', null, 10, 50.00, 120),
       (NEXT VALUE FOR purchase_order_line_id_seq, (SELECT purchase_orders.purchase_order_id from purchase_orders where supplier_name='Car Parts International'), '658421598655', 'Plastic Bumper Guard', 'blue', 1, 78.67, 26.75),
       (NEXT VALUE FOR purchase_order_line_id_seq, (SELECT purchase_orders.purchase_order_id from purchase_orders where supplier_name='USA Parts Supplier'), '333658421598', 'Exhaust Assembly', 'chrome', 1, 3000, 150),
       (NEXT VALUE FOR purchase_order_line_id_seq, (SELECT purchase_orders.purchase_order_id from purchase_orders where supplier_name='USA Parts Supplier'), '665842159854', 'Tesla Door Handles', 'green', 20, 500, 50);