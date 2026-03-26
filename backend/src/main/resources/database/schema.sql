CREATE TABLE job_postings (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,

    -- Core fields
    title            VARCHAR(120)    NOT NULL,
    company          VARCHAR(100)    NOT NULL,
    department       VARCHAR(100),
    location         VARCHAR(120),
    remote           BOOLEAN         NOT NULL DEFAULT FALSE,

    -- Classification
    job_type         VARCHAR(20)     NOT NULL,   -- FULL_TIME | PART_TIME | CONTRACT | INTERNSHIP | FREELANCE
    experience_level VARCHAR(20)     NOT NULL,   -- JUNIOR | MID | SENIOR | LEAD | EXECUTIVE
    status           VARCHAR(20)     NOT NULL DEFAULT 'DRAFT',  -- DRAFT | ACTIVE | CLOSED | ARCHIVED

    -- Compensation
    salary_min       DECIMAL(12, 2),
    salary_max       DECIMAL(12, 2),
    currency         CHAR(3)         NOT NULL DEFAULT 'USD',

    -- Content
    description      TEXT            NOT NULL,
    requirements     TEXT,
    benefits         TEXT,

    -- Lifecycle
    posted_at        TIMESTAMP,
    expires_at       TIMESTAMP,
    created_at       TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Constraints
    CONSTRAINT chk_salary_range  CHECK (salary_min IS NULL OR salary_max IS NULL OR salary_min <= salary_max),
    CONSTRAINT chk_salary_min    CHECK (salary_min IS NULL OR salary_min >= 0),
    CONSTRAINT chk_salary_max    CHECK (salary_max IS NULL OR salary_max >= 0),
    CONSTRAINT chk_job_type      CHECK (job_type IN ('FULL_TIME','PART_TIME','CONTRACT','INTERNSHIP','FREELANCE')),
    CONSTRAINT chk_exp_level     CHECK (experience_level IN ('JUNIOR','MID','SENIOR','LEAD','EXECUTIVE')),
    CONSTRAINT chk_status        CHECK (status IN ('DRAFT','ACTIVE','CLOSED','ARCHIVED'))
);

CREATE INDEX idx_job_postings_status         ON job_postings (status);
CREATE INDEX idx_job_postings_job_type       ON job_postings (job_type);
CREATE INDEX idx_job_postings_experience     ON job_postings (experience_level);
CREATE INDEX idx_job_postings_company        ON job_postings (company);
CREATE INDEX idx_job_postings_posted_at      ON job_postings (posted_at);
