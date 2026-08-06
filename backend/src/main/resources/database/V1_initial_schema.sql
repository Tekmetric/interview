
CREATE TABLE IF NOT EXISTS books (
    id           UUID            NOT NULL DEFAULT RANDOM_UUID(),
    title        VARCHAR(255)    NOT NULL,
    author       VARCHAR(255)    NOT NULL,
    isbn         VARCHAR(20)     NOT NULL,
    genre        VARCHAR(50)     NOT NULL,
    price        DECIMAL(10, 2)  NOT NULL,
    published_at DATE            NOT NULL,
    description  VARCHAR(2000),
    version      BIGINT          NOT NULL DEFAULT 0,
    created_at   TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_books       PRIMARY KEY (id),
    CONSTRAINT uq_books_isbn  UNIQUE (isbn),
    CONSTRAINT ck_books_price CHECK (price >= 0)
);

CREATE INDEX IF NOT EXISTS idx_books_author ON books (author);
CREATE INDEX IF NOT EXISTS idx_books_genre  ON books (genre);
CREATE INDEX IF NOT EXISTS idx_books_title  ON books (title);



CREATE TABLE IF NOT EXISTS idempotency_keys (
    idempotency_key VARCHAR(64)  NOT NULL,
    response_status INTEGER      NOT NULL,
    response_body   CLOB         NOT NULL,
    created_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at      TIMESTAMP    NOT NULL,

    CONSTRAINT pk_idempotency_keys PRIMARY KEY (idempotency_key)
);

CREATE INDEX IF NOT EXISTS idx_idempotency_expires ON idempotency_keys (expires_at);