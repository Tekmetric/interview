-- Sample Artists
INSERT INTO artist (name) VALUES ('Queen');
INSERT INTO artist (name) VALUES ('The Beatles');
INSERT INTO artist (name) VALUES ('Pink Floyd');

-- Sample Songs
INSERT INTO song (title, length_in_seconds, release_date, artist_id) VALUES ('Bohemian Rhapsody', 355, '1975-10-31', 1);
INSERT INTO song (title, length_in_seconds, release_date, artist_id) VALUES ('We Are The Champions', 179, '1977-10-07', 1);
INSERT INTO song (title, length_in_seconds, release_date, artist_id) VALUES ('Another One Bites the Dust', 215, '1980-08-22', 1);
INSERT INTO song (title, length_in_seconds, release_date, artist_id) VALUES ('Hey Jude', 431, '1968-08-26', 2);
INSERT INTO song (title, length_in_seconds, release_date, artist_id) VALUES ('Let It Be', 243, '1970-03-06', 2);
INSERT INTO song (title, length_in_seconds, release_date, artist_id) VALUES ('Comfortably Numb', 382, '1979-11-30', 3);
INSERT INTO song (title, length_in_seconds, release_date, artist_id) VALUES ('Wish You Were Here', 334, '1975-09-12', 3);

-- Sample Albums
INSERT INTO album (title, release_date, artist_id) VALUES ('A Night at the Opera', '1975-11-21', 1);
INSERT INTO album (title, release_date, artist_id) VALUES ('News of the World', '1977-10-28', 1);
INSERT INTO album (title, release_date, artist_id) VALUES ('The Game', '1980-06-30', 1);
INSERT INTO album (title, release_date, artist_id) VALUES ('Abbey Road', '1969-09-26', 2);
INSERT INTO album (title, release_date, artist_id) VALUES ('Let It Be', '1970-05-08', 2);
INSERT INTO album (title, release_date, artist_id) VALUES ('The Wall', '1979-11-30', 3);
INSERT INTO album (title, release_date, artist_id) VALUES ('Wish You Were Here', '1975-09-12', 3);

-- Song-Album associations
INSERT INTO song_album (album_id, song_id) VALUES (1, 1);
INSERT INTO song_album (album_id, song_id) VALUES (2, 2);
INSERT INTO song_album (album_id, song_id) VALUES (3, 3);
INSERT INTO song_album (album_id, song_id) VALUES (4, 4);
INSERT INTO song_album (album_id, song_id) VALUES (5, 5);
INSERT INTO song_album (album_id, song_id) VALUES (6, 6);
INSERT INTO song_album (album_id, song_id) VALUES (7, 7);