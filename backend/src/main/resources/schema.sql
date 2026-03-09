CREATE TABLE IF NOT EXISTS customer (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS vehicle (
    id UUID PRIMARY KEY,
    vin VARCHAR(17) UNIQUE NOT NULL,
    customer_id UUID NOT NULL,
    CONSTRAINT fk_vehicle_customer FOREIGN KEY (customer_id) REFERENCES customer(id)
);

CREATE TABLE IF NOT EXISTS work_order (
    id UUID PRIMARY KEY,
    customer_id UUID NOT NULL,
    vehicle_id UUID NOT NULL,
    scheduled_start_date_time TIMESTAMP,
    CONSTRAINT fk_work_order_customer FOREIGN KEY (customer_id) REFERENCES customer(id),
    CONSTRAINT fk_work_order_vehicle FOREIGN KEY (vehicle_id) REFERENCES vehicle(id)
);

CREATE INDEX IF NOT EXISTS idx_work_order_scheduled_start_date_time ON work_order(scheduled_start_date_time);

CREATE TABLE IF NOT EXISTS part_line_item (
    id UUID PRIMARY KEY,
    work_order_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    quantity INT NOT NULL,
    part_number UUID NOT NULL,
    CONSTRAINT fk_part_line_item_work_order FOREIGN KEY (work_order_id) REFERENCES work_order(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS labor_line_item (
    id UUID PRIMARY KEY,
    work_order_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    quantity INT NOT NULL,
    service_code UUID NOT NULL,
    CONSTRAINT fk_labor_line_item_work_order FOREIGN KEY (work_order_id) REFERENCES work_order(id) ON DELETE CASCADE
);
