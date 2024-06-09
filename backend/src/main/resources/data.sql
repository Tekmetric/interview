-- Provide SQL scripts here

DROP TABLE IF EXISTS book;
CREATE TABLE book (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(250) NOT NULL,
    author VARCHAR(250) NOT NULL,
    price FLOAT NOT NULL
);

INSERT INTO book (id, name, author, price) VALUES (1001, 'Mastering StockMarket', 'John Doe', 200.15);
INSERT INTO book (id, name, author, price) VALUES (1002, 'Leadership 1-1', 'Brian Adams', 100);


