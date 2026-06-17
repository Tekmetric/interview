CREATE TABLE work_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    price FLOAT NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    repair_order_entity_id BIGINT,
    CONSTRAINT fk_work_item_repair_order
        FOREIGN KEY (repair_order_entity_id)
        REFERENCES repair_order(id)
);
