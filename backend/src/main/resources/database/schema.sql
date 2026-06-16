-- =============================================================================
-- Schema: Customer Financing & Credit Application Service
-- Database: H2 (in-memory)
-- Note: spring.jpa.hibernate.ddl-auto=none — this script owns schema creation.
--       Executed via spring.sql.init.schema-locations before seed data.
-- =============================================================================

-- -----------------------------------------------------------------------------
-- Table: customer
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS customer (
    id                VARCHAR(36)    NOT NULL,
    first_name        VARCHAR(100)   NOT NULL,
    last_name         VARCHAR(100)   NOT NULL,
    email             VARCHAR(255)   NOT NULL,
    phone             VARCHAR(20),
    date_of_birth     DATE           NOT NULL,
    ssn               VARCHAR(11)    NOT NULL,

    -- @Embedded: Address
    address           VARCHAR(255)   NOT NULL,
    city              VARCHAR(100)   NOT NULL,
    state             CHAR(2)        NOT NULL,
    zip_code          VARCHAR(10)    NOT NULL,

    -- @Embedded: EmploymentDetails
    employment_status VARCHAR(20)    NOT NULL,
    employer_name     VARCHAR(200),
    annual_income     DECIMAL(15, 2) NOT NULL,

    -- BaseEntity audit + optimistic lock
    date_created      TIMESTAMP      NOT NULL,
    date_updated      TIMESTAMP      NOT NULL,
    version           BIGINT         NOT NULL DEFAULT 0,

    CONSTRAINT pk_customer       PRIMARY KEY (id),
    CONSTRAINT uq_customer_email UNIQUE (email)
);

CREATE INDEX IF NOT EXISTS idx_customer_last_name         ON customer (last_name);
CREATE INDEX IF NOT EXISTS idx_customer_employment_status ON customer (employment_status);
CREATE INDEX IF NOT EXISTS idx_customer_date_created      ON customer (date_created);

-- -----------------------------------------------------------------------------
-- Table: credit_application
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS credit_application (
    id                    VARCHAR(36)    NOT NULL,
    customer_id           VARCHAR(36)    NOT NULL,
    status                VARCHAR(20)    NOT NULL DEFAULT 'SUBMITTED',
    requested_loan_amount DECIMAL(15, 2) NOT NULL,
    loan_purpose          VARCHAR(30)    NOT NULL,
    monthly_debt          DECIMAL(15, 2) NOT NULL,
    notes                 VARCHAR(1000),
    submitted_at          TIMESTAMP      NOT NULL,
    decided_at            TIMESTAMP,

    -- BaseEntity audit + optimistic lock
    date_created          TIMESTAMP      NOT NULL,
    date_updated          TIMESTAMP      NOT NULL,
    version               BIGINT         NOT NULL DEFAULT 0,

    CONSTRAINT pk_credit_application     PRIMARY KEY (id),
    CONSTRAINT fk_credit_app_customer_id FOREIGN KEY (customer_id)
        REFERENCES customer (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_credit_app_customer_id     ON credit_application (customer_id);
CREATE INDEX IF NOT EXISTS idx_credit_app_status          ON credit_application (status);
CREATE INDEX IF NOT EXISTS idx_credit_app_customer_status ON credit_application (customer_id, status);
CREATE INDEX IF NOT EXISTS idx_credit_app_submitted_at    ON credit_application (submitted_at);

-- -----------------------------------------------------------------------------
-- Table: supporting_document
-- Each row stores the S3 object key for one uploaded supporting document.
-- Presigned URLs are never stored — they are generated fresh on every read.
-- Cascade delete via FK ensures rows are removed with their parent application.
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS supporting_document (
    id             VARCHAR(36)  NOT NULL,
    application_id VARCHAR(36)  NOT NULL,
    document_type  VARCHAR(40)  NOT NULL,
    object_key     VARCHAR(500) NOT NULL,
    file_name      VARCHAR(255),

    -- BaseEntity audit + optimistic lock
    date_created   TIMESTAMP    NOT NULL,
    date_updated   TIMESTAMP    NOT NULL,
    version        BIGINT       NOT NULL DEFAULT 0,

    CONSTRAINT pk_supporting_document PRIMARY KEY (id),
    CONSTRAINT fk_doc_application_id  FOREIGN KEY (application_id)
        REFERENCES credit_application (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_doc_application_id ON supporting_document (application_id);
