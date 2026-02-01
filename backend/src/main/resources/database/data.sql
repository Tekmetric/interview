-- Sample Artists
INSERT INTO artist (id, name) VALUES (1, 'Queen');
INSERT INTO artist (id, name) VALUES (2, 'The Beatles');
INSERT INTO artist (id, name) VALUES (3, 'Pink Floyd');

-- Sample Songs
INSERT INTO song (id, title, length_in_seconds, release_date, artist_id) VALUES (1, 'Bohemian Rhapsody', 355, '1975-10-31', 1);
INSERT INTO song (id, title, length_in_seconds, release_date, artist_id) VALUES (2, 'We Are The Champions', 179, '1977-10-07', 1);
INSERT INTO song (id, title, length_in_seconds, release_date, artist_id) VALUES (3, 'Another One Bites the Dust', 215, '1980-08-22', 1);
INSERT INTO song (id, title, length_in_seconds, release_date, artist_id) VALUES (4, 'Hey Jude', 431, '1968-08-26', 2);
INSERT INTO song (id, title, length_in_seconds, release_date, artist_id) VALUES (5, 'Let It Be', 243, '1970-03-06', 2);
INSERT INTO song (id, title, length_in_seconds, release_date, artist_id) VALUES (6, 'Comfortably Numb', 382, '1979-11-30', 3);
INSERT INTO song (id, title, length_in_seconds, release_date, artist_id) VALUES (7, 'Wish You Were Here', 334, '1975-09-12', 3);

-- Sample Albums
INSERT INTO album (id, title, release_date, artist_id) VALUES (1, 'A Night at the Opera', '1975-11-21', 1);
INSERT INTO album (id, title, release_date, artist_id) VALUES (2, 'News of the World', '1977-10-28', 1);
INSERT INTO album (id, title, release_date, artist_id) VALUES (3, 'The Game', '1980-06-30', 1);
INSERT INTO album (id, title, release_date, artist_id) VALUES (4, 'Abbey Road', '1969-09-26', 2);
INSERT INTO album (id, title, release_date, artist_id) VALUES (5, 'Let It Be', '1970-05-08', 2);
INSERT INTO album (id, title, release_date, artist_id) VALUES (6, 'The Wall', '1979-11-30', 3);
INSERT INTO album (id, title, release_date, artist_id) VALUES (7, 'Wish You Were Here', '1975-09-12', 3);

-- Song-Album associations
INSERT INTO song_album (album_id, song_id) VALUES (1, 1);
INSERT INTO song_album (album_id, song_id) VALUES (2, 2);
INSERT INTO song_album (album_id, song_id) VALUES (3, 3);
INSERT INTO song_album (album_id, song_id) VALUES (4, 4);
INSERT INTO song_album (album_id, song_id) VALUES (5, 5);
INSERT INTO song_album (album_id, song_id) VALUES (6, 6);
INSERT INTO song_album (album_id, song_id) VALUES (7, 7);