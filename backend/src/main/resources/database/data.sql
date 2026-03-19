-- Employee table (H2 in-memory)
CREATE TABLE IF NOT EXISTS employee (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    version         BIGINT NOT NULL DEFAULT 0,
    firstname       VARCHAR(255) NOT NULL,
    lastname        VARCHAR(255) NOT NULL,
    hired_date      DATE NOT NULL,
    gender          CHAR(1) NOT NULL,
    employment_status CHAR(1) NOT NULL,
    term_date       DATE NULL,
    yearly_salary   DECIMAL(19, 2) NOT NULL
);