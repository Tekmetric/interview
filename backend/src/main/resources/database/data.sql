INSERT INTO mechanic (name, specialization) VALUES
                                                    ('John Smith', 'Engine Repair'),
                                                    ('Jane Doe', 'Brake Systems'),
                                                    ('Mike Johnson', 'Transmission');

INSERT INTO car (license_plate, make, model, version) VALUES
                                                     ('ABC-123', 'Toyota', 'Camry', 1),
                                                     ('XYZ-789', 'Honda', 'Civic', 1),
                                                     ('DEF-456', 'Ford', 'Focus', 1);

INSERT INTO repair_order (order_date, status, car_id, version) VALUES
                                                              ('2024-01-15', 'COMPLETED', 1, 1),
                                                              ('2024-01-20', 'IN_PROGRESS', 2, 1),
                                                              ('2024-01-25', 'PLACED', 3,1);

INSERT INTO repair_order_mechanic (repair_order_id, mechanic_id) VALUES
                                                                     (1, 1),
                                                                     (1, 2),
                                                                     (2, 2),
                                                                     (3, 3);
