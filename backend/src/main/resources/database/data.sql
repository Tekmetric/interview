INSERT INTO supplier(name, supplier_type) VALUES ( 'AUDI AUTO PARTS', 'AUTO_PARTS' );
INSERT INTO supplier(name, supplier_type) VALUES ( 'Toyota auto parts', 'AUTO_PARTS' );
INSERT INTO supplier(name, supplier_type) VALUES ( 'MOTUL', 'LUBRICANTS' );
INSERT INTO supplier(name, supplier_type) VALUES ( 'CASTROL', 'LUBRICANTS' );
INSERT INTO supplier(name, supplier_type) VALUES ( 'Custom Wheels', 'WHEELS' );

INSERT INTO shop(name, location) VALUES ('Autovalex', 'Dallas');
INSERT INTO shop(name, location) VALUES ('Bimmer Shop', 'Connecticut');
INSERT INTO shop(name, location) VALUES ('Vmax Shop', 'Houston');
INSERT INTO shop(name, location) VALUES ('E-Car Shop', 'Washington');
INSERT INTO shop(name, location) VALUES ('Berlina Service', 'Austin');

INSERT INTO invoice(amount, paid, description, shop_id) VALUES ( 234, false, 'Car repair services', 2);
INSERT INTO invoice(amount, paid, description, shop_id) VALUES ( 1999, true, 'Car repair services', 2);
INSERT INTO invoice(amount, paid, description, shop_id) VALUES ( 470, true, 'Car repair services', 2);
INSERT INTO invoice(amount, paid, description, shop_id) VALUES ( 23412, true, 'Car repair services', 1);
INSERT INTO invoice(amount, paid, description, shop_id) VALUES ( 31234, true, 'Car repair services', 1);
INSERT INTO invoice(amount, paid, description, shop_id) VALUES ( 12344, true, 'Car repair services', 3);

INSERT INTO shop_suppliers(shops_id, suppliers_id) VALUES ( 1, 1 );
INSERT INTO shop_suppliers(shops_id, suppliers_id) VALUES ( 1, 2 );
INSERT INTO shop_suppliers(shops_id, suppliers_id) VALUES ( 1, 4 );
INSERT INTO shop_suppliers(shops_id, suppliers_id) VALUES ( 3, 2 );
INSERT INTO shop_suppliers(shops_id, suppliers_id) VALUES ( 3, 3 );
INSERT INTO shop_suppliers(shops_id, suppliers_id) VALUES ( 3, 5 );