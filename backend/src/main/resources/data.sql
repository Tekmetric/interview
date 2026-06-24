-- Provide SQL scripts here
INSERT INTO parts (name, inventory)
VALUES
    ('Bolt M8 x 30mm', 200),
    ('Washer M8', 200),
    ('Nut M8', 200),
    ('Nut M6', 200),
    ('Washer M6', 200),
    ('Bolt M6 x 20mm', 200);

INSERT INTO work_orders ()
VALUES
    (),
    (),
    ();

INSERT INTO work_order_parts (work_order_id, part_id, part_count)
VALUES
    (1, 1, 20),
    (1, 2, 20),
    (1, 3, 20),
    (2, 4, 30),
    (2, 5, 30),
    (2, 6, 30),
    (3, 1, 70),
    (3, 2, 70),
    (3, 3, 70),
    (3, 4, 300),
    (3, 5, 300),
    (3, 6, 300);

