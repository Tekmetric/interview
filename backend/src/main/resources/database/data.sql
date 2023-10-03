-- Provide SQL scripts here

CREATE TABLE IF NOT EXISTS BOOK
(
    ID     BIGINT AUTO_INCREMENT PRIMARY KEY,
    TITLE  VARCHAR(255),
    AUTHOR VARCHAR(255)
);

-- You can also add initial data if you want
-- INSERT INTO BOOK (TITLE, AUTHOR) VALUES ('Sample Book', 'Sample Author');

