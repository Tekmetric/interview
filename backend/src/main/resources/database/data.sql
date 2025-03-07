-- USERS
insert into t_user (id, email, first_name, last_name, password, admin)
values (nextval('hibernate_sequence'), 'admin@email.com', 'Admin', 'User',
        '$2a$12$aK4cUtA9iIeofzvo5EH52uRsGliHAHHahN6w48WZUrBC2eosoPzT6', 1);

insert into t_user (id, email, first_name, last_name, password, admin)
values (nextval('hibernate_sequence'), 'user@email.com', 'Normal', 'User',
        '$2a$12$aK4cUtA9iIeofzvo5EH52uRsGliHAHHahN6w48WZUrBC2eosoPzT6', 0);

insert into t_author (id, first_name, last_name, photo_url)
values (1, 'George', 'Orwell', 'https://cdn.britannica.com/68/9768-004-F4E88413/George-Orwell.jpg?w=300');
insert into t_book (id, name, author_id, publication_year) values (1, 'Animal Farm', 1,
                                                                   1945);
insert into t_book (id, name, author_id, publication_year) values (2, '1984', 1, 1949);

insert into t_author (id, first_name, last_name, photo_url)
values (2, 'Stephen', 'King',
        'https://upload.wikimedia.org/wikipedia/commons/thumb/e/e3/Stephen_King%2C_Comicon.jpg/220px-Stephen_King%2C_Comicon.jpg');
insert into t_book (id, name, author_id, publication_year) values (3, 'Bag of Bones', 2,
                                                                   1998);
insert into t_book (id, name, author_id, publication_year) values (4, 'The Dark Tower',
                                                                   2, 1982);
insert into t_author (id, first_name, last_name, photo_url)
values (3, 'Fyodor', 'Dostoevsky',
        'https://upload.wikimedia.org/wikipedia/commons/thumb/8/8f/Dostoevsky.jpg/452px-Dostoevsky.jpg');
insert into t_book (id, name, author_id, publication_year) values (5, 'Crime and Punishment', 3,
                                                                   1866);
insert into t_book (id, name, author_id, publication_year) values (6, 'Notes from Underground',
                                                                   3, 1864);

insert into t_author (id, first_name, last_name, photo_url)
values (4, 'J.R.R.', 'Tolkien',
        'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTw8dF9fYX4CECPfFPErmEnlArOLecH-xxyiI9WmArMI7YKPVbzgISjXkiYUhofZGsCcQo&usqp=CAU');
insert into t_book (id, name, author_id, publication_year) values (7, 'Lord of the Rings', 4, 1954);


insert into t_readinglist (id, name, last_update, shared, owner_id)
values (1, 'List-1', '2025-03-05', 0, 1);
insert into t_readinglist_book (readinglist_id, book_id) values (1, 1);
insert into t_readinglist_book (readinglist_id, book_id) values (1, 3);

insert into t_readinglist (id, name, last_update, shared, owner_id)
values (2, 'List-2', '2025-03-04', 1, 1);
insert into t_readinglist_book (readinglist_id, book_id) values (2, 2);
insert into t_readinglist_book (readinglist_id, book_id) values (2, 4);


insert into t_readinglist (id, name, last_update, shared, owner_id)
values (3, 'List-3', '2025-03-05', 0, 2);
insert into t_readinglist_book (readinglist_id, book_id) values (3, 1);
insert into t_readinglist_book (readinglist_id, book_id) values (3, 3);

insert into t_readinglist (id, name, last_update, shared, owner_id)
values (4, 'List-4', '2025-03-04', 1, 2);
insert into t_readinglist_book (readinglist_id, book_id) values (4, 2);
insert into t_readinglist_book (readinglist_id, book_id) values (4, 4);


ALTER SEQUENCE hibernate_sequence RESTART WITH 101;
