CREATE TABLE book
(
    id     BIGINT PRIMARY KEY AUTO_INCREMENT,
    title  VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL
);

CREATE TABLE review
(
    id      BIGINT PRIMARY KEY AUTO_INCREMENT,
    rating  INTEGER      NOT NULL,
    comment VARCHAR(255) NOT NULL,
    book_id BIGINT       NOT NULL,
    FOREIGN KEY (book_id) REFERENCES book (id) ON DELETE CASCADE
);

INSERT INTO book(title, author)
VALUES ('A Little Life', 'Hanya Yanagihara');
INSERT INTO review(rating, comment, book_id)
VALUES (5, 'A beautifully written novel that follows the lives of four friends.', 1);
INSERT INTO review(rating, comment, book_id)
VALUES (4, 'A thought-provoking novel that explores the complexities of human relationships.', 1);
INSERT INTO book(title, author)
VALUES ('Becoming', 'Michelle Obama');
INSERT INTO review(rating, comment, book_id)
VALUES (5, 'A captivating memoir that chronicles the former First Lady''s life journey.', 2);
