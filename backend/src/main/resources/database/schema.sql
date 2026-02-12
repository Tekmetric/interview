------------------------------------------------------------
-- EMPLOYEES TABLE
------------------------------------------------------------
CREATE TABLE employees (
    id              VARCHAR(64) PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    department      VARCHAR(100),
    email           VARCHAR(255),
    role            VARCHAR(50),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

------------------------------------------------------------
-- GOALS TABLE
------------------------------------------------------------
CREATE TABLE goals (
    id                  VARCHAR(64) PRIMARY KEY,
    employee_id         VARCHAR(64) NOT NULL,
    name                VARCHAR(255) NOT NULL,
    description         VARCHAR(2000),
    status              VARCHAR(50),
    due_date            TIMESTAMP NOT NULL,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

    CONSTRAINT fk_goals_employee
        FOREIGN KEY (employee_id)
        REFERENCES employees(id)
        ON DELETE CASCADE
);
