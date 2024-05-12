-- Provide SQL scripts here
insert into author (id, first_name, last_name) values (1, 'Dan', 'Brown');
insert into author (id, first_name, last_name) values (2, 'Stephen', 'King');
insert into author (id, first_name, last_name) values (3, 'Neil', 'Gaiman');

alter sequence author_seq restart with 4;

insert into book (id, title, author_id, price) values (1, 'The Da Vinci''s code', 1, 15);
insert into book (id, title, author_id, price) values (2, 'Angels and Demons', 1, 16.5);
insert into book (id, title, author_id, price) values (3, 'The Shining', 2, 10);
insert into book (id, title, author_id, price) values (4, 'Pet Sematary', 2, 12);
insert into book (id, title, author_id, price) values (5, 'It', 2, 10.99);
insert into book (id, title, author_id, price) values (6, 'American Gods', 3, 20);

alter sequence book_seq restart with 7;

insert into book_details (book_id, isbn, publication_date, page_count, description) values (1, '978-0307474278', '2003-03-18', 481, 'A gripping mystery thriller involving codes, symbols, and religious secrets.');
insert into book_details (book_id, isbn, publication_date, page_count, description) values (2, '978-0743493468', '2000-05-01', 736, 'Intrigue unfolds as Harvard symbologist Robert Langdon uncovers Vatican conspiracies.');
insert into book_details (book_id, isbn, publication_date, page_count, description) values (3, '978-0307743657', '1977-01-28', 636, 'A psychological horror novel set in an isolated hotel with sinister secrets.');
insert into book_details (book_id, isbn, publication_date, page_count, description) values (4, '978-0307743725', '1983-11-14', 576, 'A chilling tale of supernatural horror involving grief, resurrection, and ancient burial grounds.');
insert into book_details (book_id, isbn, publication_date, page_count, description) values (5, '978-1501175466', '1986-09-15', 1184, 'A terrifying saga of childhood fears, friendship, and a shapeshifting evil in Derry, Maine.');
insert into book_details (book_id, isbn, publication_date, page_count, description) values (6, '978-0060558123', '2001-06-09', 624, 'A mythological fantasy where ancient deities clash with modern America''s beliefs and obsessions.');

insert into book_review (id, book_id, score, text) values (1, 1, 4, 'Exciting plot twists and intriguing historical references.');
insert into book_review (id, book_id, score, text) values (2, 1, 5, 'Couldn''t put it down! Brilliantly written.');
insert into book_review (id, book_id, score, text) values (3, 1, 3, 'Interesting concept but pacing felt slow at times.');
insert into book_review (id, book_id, score, text) values (4, 2, 5, 'Thrilling plot with unexpected twists and turns.');
insert into book_review (id, book_id, score, text) values (5, 2, 4, 'Fascinating blend of science and religion.');
insert into book_review (id, book_id, score, text) values (6, 2, 4, 'Compelling characters and intricate plot.');
insert into book_review (id, book_id, score, text) values (7, 3, 5, 'Terrifyingly atmospheric, a horror masterpiece.');
insert into book_review (id, book_id, score, text) values (8, 3, 3, 'Slow start but builds to a chilling climax.');
insert into book_review (id, book_id, score, text) values (9, 3, 4, 'Captivating story, kept me on edge throughout.');
insert into book_review (id, book_id, score, text) values (10, 4, 4, 'Spine-tingling horror with unforgettable scenes.');
insert into book_review (id, book_id, score, text) values (11, 4, 5, 'Creepy and unsettling, a must-read for horror fans.');
insert into book_review (id, book_id, score, text) values (12, 4, 3, 'Good but not as scary as expected.');
insert into book_review (id, book_id, score, text) values (13, 5, 5, 'Absolutely terrifying, King at his best!');
insert into book_review (id, book_id, score, text) values (14, 5, 4, 'Epic tale of friendship and facing childhood fears.');
insert into book_review (id, book_id, score, text) values (15, 5, 5, 'Masterful storytelling, kept me up at night.');
insert into book_review (id, book_id, score, text) values (16, 6, 4, 'Intriguing premise with richly developed characters.');
insert into book_review (id, book_id, score, text) values (17, 6, 5, 'Immersive world-building, Neil Gaiman''s brilliance shines.');
insert into book_review (id, book_id, score, text) values (18, 6, 3, 'Started strong but lost momentum towards the end.');

alter sequence review_seq restart with 19;