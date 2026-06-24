CREATE TABLE customer_invoices
(
    invoice_number       VARCHAR(50) PRIMARY KEY,
    version              INT         NOT NULL,
    status               VARCHAR(15) NOT NULL,
    customer_id          VARCHAR(64) NOT NULL,
    notes                TEXT,
    payment_instructions TEXT,
    issue_date           DATE        NOT NULL,
    due_date             DATE,
    paid_date            DATE,
    created_at           TIMESTAMP   NOT NULL,
    created_by           VARCHAR(64) NOT NULL,
    updated_at           TIMESTAMP   NOT NULL,
    updated_by           VARCHAR(64) NOT NULL
);

CREATE INDEX idx_invoice_customer ON customer_invoices (customer_id);
CREATE INDEX idx_invoice_status ON customer_invoices (status);

CREATE TABLE customer_invoice_items
(
    invoice_number VARCHAR(50)    NOT NULL,
    item_code      VARCHAR(30),
    item_name      VARCHAR(250)   NOT NULL,
    quantity       DECIMAL(10, 2) NOT NULL,
    unit_price     DECIMAL(10, 2) NOT NULL,
    tax_rate       DECIMAL(5, 2)  NOT NULL,
    CONSTRAINT fk_invoice_items_invoice
        FOREIGN KEY (invoice_number)
            REFERENCES customer_invoices (invoice_number)
            ON DELETE CASCADE
);