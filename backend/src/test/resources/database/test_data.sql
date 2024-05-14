insert into author (id, first_name, last_name) values (1, 'Author', 'One');

alter sequence author_seq restart with 2;

insert into book (id, title, author_id, price) values (1, 'Book: Vol. I', 1, 10);
insert into book (id, title, author_id, price) values (2, 'Book: Vol. II', 1, 11);
insert into book (id, title, author_id, price) values (3, 'Book: Vol. III', 1, 12);

alter sequence book_seq restart with 4;

insert into book_details (book_id, isbn, publication_date, page_count, description) values (1, '978-0307474278', '2011-01-01', 300, 'Vol. I book description.');
insert into book_details (book_id, isbn, publication_date, page_count, description) values (2, '978-0743493468', '2012-02-02', 400, 'Vol. II book description.');
insert into book_details (book_id, isbn, publication_date, page_count, description) values (3, '978-0307743657', '2013-03-03', 500, 'Vol. III book description.');

insert into book_review (id, book_id, score, text) values (1, 1, 4, 'Exciting book.');
insert into book_review (id, book_id, score, text) values (2, 1, 5, 'Brilliantly written.');
insert into book_review (id, book_id, score, text) values (3, 1, 3, 'Interesting concept.');
insert into book_review (id, book_id, score, text) values (4, 2, 5, 'Thrilling plot.');

alter sequence review_seq restart with 5;