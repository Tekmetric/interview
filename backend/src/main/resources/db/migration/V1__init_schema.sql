CREATE TABLE customers (
    id         UUID      DEFAULT RANDOM_UUID(7) PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    email      VARCHAR(255) NOT NULL,
    phone      VARCHAR(50),
    version    INT       NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_customer_email UNIQUE (email)
);

CREATE INDEX idx_customer_name ON customers(name);

CREATE TABLE repair_orders (
    id          UUID      DEFAULT RANDOM_UUID(7) PRIMARY KEY,
    description   VARCHAR(255) NOT NULL,
    status        VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    -- Vehicle info is denormalized here rather than in a separate table.
    -- A vehicles table would be warranted if we needed per-vehicle service history
    -- or customer-to-vehicle reuse across orders.
    vehicle_make  VARCHAR(100) NOT NULL,
    vehicle_model VARCHAR(100) NOT NULL,
    vehicle_year  INT          NOT NULL,
    license_plate VARCHAR(20),
    customer_id   UUID         NOT NULL,
    version     INT       NOT NULL DEFAULT 0,
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_repair_order_customer FOREIGN KEY (customer_id) REFERENCES customers(id)
);

CREATE INDEX idx_repair_order_customer_id ON repair_orders(customer_id);
CREATE INDEX idx_repair_order_status ON repair_orders(status);
CREATE INDEX idx_repair_order_vehicle_make ON repair_orders(vehicle_make);
CREATE INDEX idx_repair_order_license_plate ON repair_orders(license_plate);
CREATE INDEX idx_repair_order_created_at ON repair_orders(created_at);
CREATE INDEX idx_repair_order_updated_at ON repair_orders(updated_at);

CREATE TABLE line_items (
    id              UUID           DEFAULT RANDOM_UUID(7) PRIMARY KEY,
    description     VARCHAR(255)   NOT NULL,
    unit_price      DECIMAL(10, 2) NOT NULL,
    repair_order_id UUID           NOT NULL,
    version         INT       NOT NULL DEFAULT 0,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_line_item_repair_order FOREIGN KEY (repair_order_id) REFERENCES repair_orders(id)
);

CREATE INDEX idx_line_item_repair_order_id ON line_items(repair_order_id);
