
INSERT INTO car (make, model, model_year, color, vin) VALUES
  ('Toyota', 'Camry', 2022, 'White', '12345678901234567'),
  ('Honda', 'Accord', 2021, 'Black', '23456789012345678');


INSERT INTO customer (first_name, last_name, email) VALUES
('John', 'Doe', 'john.doe@example.com'),
('Jane', 'Smith', 'jane.smith@example.com');


INSERT INTO car_customer(car_id, customer_id) VALUES
(1,1),
(2,1),
(2,2);

