CREATE SEQUENCE revisions_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE revisions (
        id INT NOT NULL PRIMARY KEY,
        time_stamp      TIMESTAMP DEFAULT NULL
        );

CREATE TABLE repair_orders (
            id              VARCHAR(36) PRIMARY KEY NOT NULL,
            shop_id         VARCHAR(36) NOT NULL,
            external_RO     VARCHAR(50) NOT NULL,
            status          VARCHAR(32) NOT NULL,
            created_date    TIMESTAMP    NOT NULL,
            odometer_in     INT,
            odometer_out    INT,
            notes           varchar(1000),
            created_at      TIMESTAMP NOT NULL,
            modified_at     TIMESTAMP NOT NULL
            );

CREATE TABLE audit_repair_orders (
            id              VARCHAR(36)  NOT NULL,
            revision_id     INT NOT NULL,
            revision_type_id          TINYINT,
            shop_id         VARCHAR(36) ,
            external_RO     VARCHAR(50) ,
            status          VARCHAR(32) ,
            created_date    TIMESTAMP   ,
            odometer_in     INT,
            odometer_out    INT,
            notes           varchar(1000),
            created_at      TIMESTAMP ,
            modified_at     TIMESTAMP ,
            CONSTRAINT pk_audit_repair_orders PRIMARY KEY (id, revision_id),
            CONSTRAINT fk_repair_order_revisions FOREIGN KEY (revision_id) REFERENCES revisions(id)
            );

CREATE TABLE repair_order_lines (
            id              VARCHAR(36) PRIMARY KEY NOT NULL,
            repair_order_id VARCHAR(36) NOT NULL,
            description     VARCHAR(255) NOT NULL,
            quantity        DECIMAL(10,2) DEFAULT 0,
            unit_price      DECIMAL(10,2) DEFAULT 0,
            created_at      TIMESTAMP NOT NULL,
            modified_at     TIMESTAMP NOT NULL,
            CONSTRAINT fk_repair_order FOREIGN KEY (repair_order_id) REFERENCES repair_orders(id)
            );

CREATE TABLE audit_repair_order_lines (
            id              VARCHAR(36) NOT NULL,
            revision_id     INT NOT NULL,
            revision_type_id          TINYINT,
            repair_order_id VARCHAR(36),
            description     VARCHAR(255),
            quantity        DECIMAL(10,2) DEFAULT 0,
            unit_price      DECIMAL(10,2) DEFAULT 0,
            created_at      TIMESTAMP,
            modified_at     TIMESTAMP,
            CONSTRAINT pk_audit_repair_order_lines PRIMARY KEY (id, revision_id),
            CONSTRAINT fk_repair_order_line_revisions FOREIGN KEY (revision_id) REFERENCES revisions(id)
            );
