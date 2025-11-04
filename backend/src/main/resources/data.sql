-- Provide SQL scripts here
INSERT INTO parts (id, name, inventory)
VALUES
    (1, 'Bolt M8 x 30mm', 200),
    (2, 'Washer M8', 200),
    (3, 'Nut M8', 200),
    (4, 'Nut M6', 200),
    (5, 'Washer M6', 200),
    (6, 'Bolt M6 x 20mm', 200);

INSERT INTO work_orders (id)
VALUES
    (1),
    (2),
    (3);

INSERT INTO work_order_parts (id, work_order_id, part_id, part_count)
VALUES
    (1, 1, 1, 20),
    (2, 1, 2, 20),
    (3, 1, 3, 20),
    (4, 2, 4, 30),
    (5, 2, 5, 30),
    (6, 2, 6, 30),
    (7, 3, 1, 70),
    (8, 3, 2, 70),
    (9, 3, 3, 70),
    (10, 3, 4, 300),
    (11, 3, 5, 300),
    (12, 3, 6, 300);

