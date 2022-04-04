-- Provide SQL scripts here
CREATE TABLE book (
                    id                   INT           NOT NULL,
                    title                VARCHAR(50)   NOT NULL,
                    isbn                 VARCHAR(20)   NOT NULL,
                    publicationYear      INT           NOT NULL,
                    publisher            VARCHAR(50)   NOT NULL,
                    PRIMARY KEY (id)
);

CREATE TABLE author (
                      id        INT           NOT NULL,
                      name      VARCHAR(50)   NOT NULL,
                      PRIMARY KEY (id)
);