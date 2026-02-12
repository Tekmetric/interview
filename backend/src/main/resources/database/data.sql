INSERT INTO repair_order (
    vin,
    car_model,
    status,
    issue_description,
    is_deleted,
    created_at,
    updated_at
) VALUES ('1HGCM82633A004352','Honda Civic','DRAFT','Engine makes a knocking sound', FALSE,NOW(), NOW());

INSERT INTO work_item (
    name,
    description,
    price,
    is_deleted,
    created_at,
    updated_at,
    repair_order_entity_id
) VALUES ('Oil Change', 'Replace engine oil and oil filter', 79.99, FALSE, NOW(), NOW(), 1);

INSERT INTO work_item (
    name,
    description,
    price,
    is_deleted,
    created_at,
    updated_at,
    repair_order_entity_id
) VALUES ('Brake Pads Replacement', 'Replace front and rear brake pads', 250.00, FALSE, NOW(), NOW(), 1);