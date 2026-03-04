CREATE INDEX idx_work_orders_customer_id_id
    ON work_orders (customer_id, id);
