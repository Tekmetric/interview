INSERT INTO customers (first_name, last_name, phone) VALUES ('Satsuki', 'Kusakabe', '555-0101');
INSERT INTO customers (first_name, last_name, phone) VALUES ('Mei', 'Kusakabe', '555-0102');
INSERT INTO customers (first_name, last_name, phone) VALUES ('Chihiro', 'Ogino', '555-0103');
INSERT INTO customers (first_name, last_name, phone) VALUES ('Haku', 'Nigihayami', '555-0104');
INSERT INTO customers (first_name, last_name, phone) VALUES ('Sophie', 'Hatter', '555-0105');
INSERT INTO customers (first_name, last_name, phone) VALUES ('Howl', 'Jenkins', '555-0106');
INSERT INTO customers (first_name, last_name, phone) VALUES ('San', 'Princess', '555-0107');
INSERT INTO customers (first_name, last_name, phone) VALUES ('Ashitaka', 'Emishi', '555-0108');
INSERT INTO customers (first_name, last_name, phone) VALUES ('Kiki', 'Delivery', '555-0109');
INSERT INTO customers (first_name, last_name, phone) VALUES ('Ponyo', 'Fujimoto', '555-0110');

INSERT INTO vehicles (vin, make, model, model_year, customer_id) VALUES ('TOTORO12345678901', 'Mazda', 'Bongo', 1988, 1);
INSERT INTO vehicles (vin, make, model, model_year, customer_id) VALUES ('CATBUS98765432102', 'Toyota', 'Hiace', 1988, 1);
INSERT INTO vehicles (vin, make, model, model_year, customer_id) VALUES ('MEIMOBILE12345673', 'Honda', 'Today', 1988, 2);
INSERT INTO vehicles (vin, make, model, model_year, customer_id) VALUES ('SPIRITAWAY1234564', 'Audi', 'A4', 2001, 3);
INSERT INTO vehicles (vin, make, model, model_year, customer_id) VALUES ('DRAGONFLIGHT12345', 'Subaru', 'Legacy', 2001, 4);
INSERT INTO vehicles (vin, make, model, model_year, customer_id) VALUES ('MOVINGCASTLE12346', 'Rover', 'Mini', 2004, 5);
INSERT INTO vehicles (vin, make, model, model_year, customer_id) VALUES ('WIZARDFIRE1234567', 'Jaguar', 'E-Type', 2004, 6);
INSERT INTO vehicles (vin, make, model, model_year, customer_id) VALUES ('WOLFGIRL123456789', 'Jeep', 'Wrangler', 1997, 7);
INSERT INTO vehicles (vin, make, model, model_year, customer_id) VALUES ('DEERGOD1234567890', 'Toyota', 'LandCruiser', 1997, 8);
INSERT INTO vehicles (vin, make, model, model_year, customer_id) VALUES ('BROOMSTICK1234561', 'Vespa', 'Primavera', 1989, 9);
INSERT INTO vehicles (vin, make, model, model_year, customer_id) VALUES ('OCEANWAVE12345671', 'Nissan', 'Figaro', 2008, 10);

INSERT INTO service_order (description, status, created_at) VALUES ('Oil change and filter replacement', 'COMPLETED', CURRENT_TIMESTAMP);
INSERT INTO service_order (description, status, created_at) VALUES ('Brake pad resurfacing', 'COMPLETED', CURRENT_TIMESTAMP);
INSERT INTO service_order (description, status, created_at) VALUES ('Engine diagnostic', 'COMPLETED', CURRENT_TIMESTAMP);
INSERT INTO service_order (description, status, created_at) VALUES ('Tire rotation and alignment', 'COMPLETED', CURRENT_TIMESTAMP);
INSERT INTO service_order (description, status, created_at) VALUES ('Transmission fluid flush', 'COMPLETED', CURRENT_TIMESTAMP);
INSERT INTO service_order (description, status, created_at) VALUES ('Replace spark plugs', 'COMPLETED', CURRENT_TIMESTAMP);
INSERT INTO service_order (description, status, created_at) VALUES ('Battery load test', 'PENDING', CURRENT_TIMESTAMP);
INSERT INTO service_order (description, status, created_at) VALUES ('Cabin air filter replacement', 'PENDING', CURRENT_TIMESTAMP);
INSERT INTO service_order (description, status, created_at) VALUES ('Suspension strut replacement', 'PENDING', CURRENT_TIMESTAMP);
INSERT INTO service_order (description, status, created_at) VALUES ('Full detailing and waxing', 'PENDING', CURRENT_TIMESTAMP);

INSERT INTO service_order_vehicle (service_order_id, vehicle_id) VALUES (1, 4);
INSERT INTO service_order_vehicle (service_order_id, vehicle_id) VALUES (2, 4);
INSERT INTO service_order_vehicle (service_order_id, vehicle_id) VALUES (3, 4);
INSERT INTO service_order_vehicle (service_order_id, vehicle_id) VALUES (4, 4);
INSERT INTO service_order_vehicle (service_order_id, vehicle_id) VALUES (5, 4);
INSERT INTO service_order_vehicle (service_order_id, vehicle_id) VALUES (6, 4);
INSERT INTO service_order_vehicle (service_order_id, vehicle_id) VALUES (7, 4);
INSERT INTO service_order_vehicle (service_order_id, vehicle_id) VALUES (8, 4);
INSERT INTO service_order_vehicle (service_order_id, vehicle_id) VALUES (9, 4);
INSERT INTO service_order_vehicle (service_order_id, vehicle_id) VALUES (10, 4);

INSERT INTO service_order_vehicle (service_order_id, vehicle_id) VALUES (1, 7);
INSERT INTO service_order_vehicle (service_order_id, vehicle_id) VALUES (4, 7);
INSERT INTO service_order_vehicle (service_order_id, vehicle_id) VALUES (10, 7);