-- Provide SQL scripts here


-- VINYL RECORDS:
INSERT INTO VINYL_RECORD ("ACQUISITION_DATE", "NUMBER_OF_DISCS", "EAN_CODE", "GENRE", "LABEL", "RELEASE_YEAR", "TITLE",
                          "ALBUM_TYPE")
VALUES ('2023-09-01', 1, '1234567890123', 'Rock', 'Atlantic Records', '1972', 'Led Zeppelin IV', 'ALBUM'),
       ('2023-08-15', 2, '2345678901234', 'Jazz', 'Blue Note Records', '1959', 'Kind of Blue', 'ALBUM'),
       ('2023-07-30', 1, '3456789012345', 'Pop', 'Capitol Records', '1983', 'Thriller', 'ALBUM'),
       ('2023-09-05', 1, '4567890123456', 'Electronic', 'Warp Records', '1994', 'Selected Ambient Works', 'ALBUM'),
       ('2023-08-25', 3, '5678901234567', 'Classical', 'Deutsche Grammophon', '1824', 'Beethoven: Symphony No.9',
        'COMPILATION'),
       ('2023-09-10', 1, '6789012345678', 'Hip Hop', 'Def Jam', '2003', 'The Black Album', 'ALBUM'),
       ('2023-07-21', 1, '7890123456789', 'Folk', 'Columbia Records', '1965', 'Highway 61 Revisited', 'ALBUM'),
       ('2023-09-12', 1, '8901234567890', 'Rock', 'EMI', '1977', 'The Dark Side of the Moon', 'ALBUM'),
       ('2023-09-18', 1, '9012345678901', 'Pop', 'RCA', '2019', 'Future Nostalgia', 'ALBUM'),
       ('2023-08-10', 2, '0123456789012', 'Soul', 'Motown', '1971', 'What''s Going On', 'ALBUM'),
       ('2023-09-20', 1, '1123456789013', 'Electronic', 'Mute Records', '1978', 'Warm Leatherette', 'SINGLE'),
       ('2023-09-22', 2, '1223456789014', 'Rock', 'Sub Pop', '1991', 'Nevermind', 'ALBUM'),
       ('2023-08-02', 1, '1323456789015', 'Jazz', 'Verve Records', '1964', 'Getz/Gilberto', 'ALBUM'),
       ('2023-09-08', 1, '1423456789016', 'Pop', 'Island Records', '1987', 'The Joshua Tree', 'ALBUM'),
       ('2023-07-28', 1, '1523456789017', 'Hip Hop', 'Aftermath', '2015', 'Compton', 'ALBUM'),
       ('2023-09-11', 1, '1623456789018', 'Pop', 'Interscope', '2020', 'Chromatica', 'ALBUM'),
       ('2023-09-13', 1, '1723456789019', 'Rock', 'Geffen', '1987', 'Appetite for Destruction', 'ALBUM'),
       ('2023-08-18', 1, '1823456789020', 'Classical', 'Sony Classical', '1723', 'Vivaldi: The Four Seasons',
        'COMPILATION'),
       ('2023-09-09', 1, '1923456789021', 'Electronic', 'XL Recordings', '2016', 'A Moon Shaped Pool', 'ALBUM'),
       ('2023-09-16', 1, '2023456789022', 'Folk', 'Warner Bros.', '1969', 'Abbey Road', 'ALBUM');

-- ARTISTS:

INSERT INTO PUBLIC.ARTIST ("IMAGE_URL", "NAME")
VALUES ('https://example.com/led_zeppelin.jpg', 'Led Zeppelin'),
       ('https://example.com/miles_davis.jpg', 'Miles Davis'),
       ('https://example.com/michael_jackson.jpg', 'Michael Jackson'),
       ('https://example.com/aphex_twin.jpg', 'Aphex Twin'),
       ('https://example.com/beethoven.jpg', 'Ludwig van Beethoven'),
       ('https://example.com/jay_z.jpg', 'Jay-Z'),
       ('https://example.com/bob_dylan.jpg', 'Bob Dylan'),
       ('https://example.com/pink_floyd.jpg', 'Pink Floyd'),
       ('https://example.com/dua_lipa.jpg', 'Dua Lipa'),
       ('https://example.com/marvin_gaye.jpg', 'Marvin Gaye'),
       ('https://example.com/the_normal.jpg', 'The Normal'),
       ('https://example.com/nirvana.jpg', 'Nirvana'),
       ('https://example.com/stan_getz.jpg', 'Stan Getz & João Gilberto'),
       ('https://example.com/u2.jpg', 'U2'),
       ('https://example.com/dr_dre.jpg', 'Dr. Dre'),
       ('https://example.com/lady_gaga.jpg', 'Lady Gaga'),
       ('https://example.com/guns_n_roses.jpg', 'Guns N'' Roses'),
       ('https://example.com/antonio_vivaldi.jpg', 'Antonio Vivaldi'),
       ('https://example.com/radiohead.jpg', 'Radiohead'),
       ('https://example.com/the_beatles.jpg', 'The Beatles');

-- MANY-TO-MANY JOIN TABLE BTW vinyl_record and artist:

INSERT INTO PUBLIC.VINYL_RECORD_ARTIST ("ARTISTS_ID", "RECORD_ID")
VALUES (1, 1);

INSERT INTO PUBLIC.VINYL_RECORD_ARTIST ("ARTISTS_ID", "RECORD_ID")
VALUES (2, 2);

INSERT INTO PUBLIC.VINYL_RECORD_ARTIST ("ARTISTS_ID", "RECORD_ID")
VALUES (3, 3);

INSERT INTO PUBLIC.VINYL_RECORD_ARTIST ("ARTISTS_ID", "RECORD_ID")
VALUES (4, 4);

INSERT INTO PUBLIC.VINYL_RECORD_ARTIST ("ARTISTS_ID", "RECORD_ID")
VALUES (5, 5);

INSERT INTO PUBLIC.VINYL_RECORD_ARTIST ("ARTISTS_ID", "RECORD_ID")
VALUES (6, 6);

INSERT INTO PUBLIC.VINYL_RECORD_ARTIST ("ARTISTS_ID", "RECORD_ID")
VALUES (7, 7);

INSERT INTO PUBLIC.VINYL_RECORD_ARTIST ("ARTISTS_ID", "RECORD_ID")
VALUES (8, 8);

INSERT INTO PUBLIC.VINYL_RECORD_ARTIST ("ARTISTS_ID", "RECORD_ID")
VALUES (9, 9);

INSERT INTO PUBLIC.VINYL_RECORD_ARTIST ("ARTISTS_ID", "RECORD_ID")
VALUES (10, 10);

INSERT INTO PUBLIC.VINYL_RECORD_ARTIST ("ARTISTS_ID", "RECORD_ID")
VALUES (11, 11);

INSERT INTO PUBLIC.VINYL_RECORD_ARTIST ("ARTISTS_ID", "RECORD_ID")
VALUES (12, 12);

INSERT INTO PUBLIC.VINYL_RECORD_ARTIST ("ARTISTS_ID", "RECORD_ID")
VALUES (13, 13);

INSERT INTO PUBLIC.VINYL_RECORD_ARTIST ("ARTISTS_ID", "RECORD_ID")
VALUES (14, 14);

INSERT INTO PUBLIC.VINYL_RECORD_ARTIST ("ARTISTS_ID", "RECORD_ID")
VALUES (15, 15);

INSERT INTO PUBLIC.VINYL_RECORD_ARTIST ("ARTISTS_ID", "RECORD_ID")
VALUES (16, 16);

INSERT INTO PUBLIC.VINYL_RECORD_ARTIST ("ARTISTS_ID", "RECORD_ID")
VALUES (17, 17);

INSERT INTO PUBLIC.VINYL_RECORD_ARTIST ("ARTISTS_ID", "RECORD_ID")
VALUES (18, 18);

INSERT INTO PUBLIC.VINYL_RECORD_ARTIST ("ARTISTS_ID", "RECORD_ID")
VALUES (19, 19);

INSERT INTO PUBLIC.VINYL_RECORD_ARTIST ("ARTISTS_ID", "RECORD_ID")
VALUES (20, 20);

-- to be sure that there are no artists already added, let's add a record with several artists that were not added yet:

INSERT INTO PUBLIC.VINYL_RECORD ("ACQUISITION_DATE", "NUMBER_OF_DISCS", "EAN_CODE", "GENRE", "LABEL", "RELEASE_YEAR", "TITLE", "ALBUM_TYPE")
VALUES ('2023-09-15', 2, '2023456789024', 'Electronic', 'Columbia', '2013', 'Random Access Memories', 'ALBUM');
INSERT INTO PUBLIC.ARTIST ("IMAGE_URL", "NAME")
VALUES
    ('https://example.com/daft_punk.jpg', 'Daft Punk'),
    ('https://example.com/pharrell_williams.jpg', 'Pharrell Williams'),
    ('https://example.com/nile_rodgers.jpg', 'Nile Rodgers'),
    ('https://example.com/julian_casablancas.jpg', 'Julian Casablancas');

INSERT INTO PUBLIC.VINYL_RECORD_ARTIST ("ARTISTS_ID", "RECORD_ID")
VALUES
    (21, 21),
    (22, 21),
    (23, 21),
    (24, 21);


INSERT INTO PUBLIC.ALBUM_PHOTO ("RANK", "RECORD_ID", "IMAGE_URL", "UUID")
VALUES
    ('COVER', 1, 'https://example.com/led_zeppelin_iv_cover.jpg', 'b16f4d88-9e22-421e-bd80-fcb048c91267'),
    ('BACK', 1, 'https://example.com/led_zeppelin_iv_back.jpg', 'a9fbdc67-4367-41d9-80f2-678d8e10e333'),

    ('COVER', 2, 'https://example.com/kind_of_blue_cover.jpg', '2b571a65-6a56-4d3d-8b6b-7744e507ebf5'),
    ('BACK', 2, 'https://example.com/kind_of_blue_back.jpg', 'd21d2e67-10d4-4965-967b-3424b46be7d0'),

    ('COVER', 3, 'https://example.com/thriller_cover.jpg', 'c879af7a-2bcf-4d6d-9d39-313c8c93c028'),
    ('BACK', 3, 'https://example.com/thriller_back.jpg', '1d439f9a-41e3-49d9-a841-5585393b25de'),

    ('COVER', 4, 'https://example.com/selected_ambient_works_cover.jpg', '0425d92e-8a63-40b4-a90d-10a60d4b3829'),
    ('BACK', 4, 'https://example.com/selected_ambient_works_back.jpg', '1ef79d65-ccdd-4f7c-800a-7604684f55b6'),

    ('COVER', 5, 'https://example.com/beethoven_symphony_9_cover.jpg', '08f20f65-9cb7-4cd7-8b74-8165a87b1182'),
    ('BACK', 5, 'https://example.com/beethoven_symphony_9_back.jpg', '01fd0dcf-468a-44fd-a963-80d507510eaf'),

    ('COVER', 6, 'https://example.com/the_black_album_cover.jpg', '54c2d1e7-b4f7-4021-aaf7-82347cbe19af'),
    ('BACK', 6, 'https://example.com/the_black_album_back.jpg', 'ace7201a-439a-4a28-a6de-40f724cd06c4'),

    ('COVER', 7, 'https://example.com/highway_61_revisited_cover.jpg', 'ed153519-3ff3-4a9d-9f3e-2de36697849f'),
    ('BACK', 7, 'https://example.com/highway_61_revisited_back.jpg', 'cf61c79c-9dd1-41fd-89a3-3b4b3d1130ec'),

    ('COVER', 8, 'https://example.com/dark_side_of_the_moon_cover.jpg', '179fcb5a-e637-46c7-bcb3-1d08a7ec3c95'),
    ('BACK', 8, 'https://example.com/dark_side_of_the_moon_back.jpg', '6077d8de-b5f6-48ff-b32c-daa74b97fc61'),

    ('COVER', 9, 'https://example.com/future_nostalgia_cover.jpg', 'b1b981ac-bce7-46b3-8885-b392872481cb'),
    ('BACK', 9, 'https://example.com/future_nostalgia_back.jpg', 'ea0d7c03-f7b6-4b2e-bf46-4ecac2735c6f'),

    ('COVER', 10, 'https://example.com/whats_going_on_cover.jpg', '893cb7db-29ae-4a79-a1a3-eec0d52e0b8e'),
    ('BACK', 10, 'https://example.com/whats_going_on_back.jpg', '43dc1e2e-6b62-4fb6-b00b-e2f6ed5717c5'),

    ('COVER', 11, 'https://example.com/warm_leatherette_cover.jpg', '0ddbc8b6-9f5e-47e1-a388-2730029efca0'),
    ('BACK', 11, 'https://example.com/warm_leatherette_back.jpg', 'd25d7c46-212f-4780-920f-832239b96d67'),

    ('COVER', 12, 'https://example.com/nevermind_cover.jpg', 'e4049141-c18b-4e9c-8e4e-1c8a2fe143f4'),
    ('BACK', 12, 'https://example.com/nevermind_back.jpg', '3b5018fd-d11c-49e2-9234-45121d125e26'),

    ('COVER', 13, 'https://example.com/getz_gilberto_cover.jpg', '33fd2563-4e12-4197-9332-f8b3e7073c15'),
    ('BACK', 13, 'https://example.com/getz_gilberto_back.jpg', 'ee07ce82-b92b-4ce3-a349-5af665c4a171'),

    ('COVER', 14, 'https://example.com/the_joshua_tree_cover.jpg', '1e1239c0-31a7-47e5-b462-bc048dc06b8a'),
    ('BACK', 14, 'https://example.com/the_joshua_tree_back.jpg', '60246fd4-5a9f-4324-9159-007feab9cdb4'),

    ('COVER', 15, 'https://example.com/compton_cover.jpg', 'a2090e89-1f3b-4cf3-9adf-12eeb5e93ca1'),
    ('BACK', 15, 'https://example.com/compton_back.jpg', '77f9c292-9ac3-4824-8b41-e51e603a2108'),

    ('COVER', 16, 'https://example.com/chromatica_cover.jpg', '3d8bb583-775a-4c79-80b9-871b5bfb7700'),
    ('BACK', 16, 'https://example.com/chromatica_back.jpg', '43b67a55-2c9e-4cb7-b21e-670bd9143735'),

    ('COVER', 17, 'https://example.com/appetite_for_destruction_cover.jpg', '22c9a366-5e84-4a20-a272-605f221a2ca3'),
    ('BACK', 17, 'https://example.com/appetite_for_destruction_back.jpg', '99dfd630-1d7a-4a75-8bc6-47953eebf002'),

    ('COVER', 18, 'https://example.com/vivaldi_four_seasons_cover.jpg', '8bc88b13-3709-46a7-9b8e-98e407058ff0'),
    ('BACK', 18, 'https://example.com/vivaldi_four_seasons_back.jpg', '88f0fe99-9ba2-4821-a289-2f4e6dbfdbcf'),

    ('COVER', 19, 'https://example.com/a_moon_shaped_pool_cover.jpg', 'd3b17f5b-e5a4-4dc4-b7de-0f86c5831709'),
    ('BACK', 19, 'https://example.com/a_moon_shaped_pool_back.jpg', 'c0db08a1-e8d1-4939-b75e-ffef405a7c63'),

    ('COVER', 20, 'https://example.com/abbey_road_cover.jpg', 'db0899ea-56c1-469b-96de-d8ed23a558c2'),
    ('BACK', 20, 'https://example.com/abbey_road_back.jpg', '2a963b8d-8004-4e82-9f02-bf85d42f4229');

INSERT INTO PUBLIC.ALBUM_PHOTO ("RANK", "RECORD_ID", "IMAGE_URL", "UUID")
VALUES
    ('COVER', 21, 'https://example.com/random_access_memories_cover.jpg', 'd66a9673-b492-4e70-bcd8-c41f89f3d0fb'),
    ('BACK', 21, 'https://example.com/random_access_memories_back.jpg', '7c4f78f1-6df2-4d36-a5b1-50fae990b365'),
    ('OTHER', 21, 'https://example.com/random_access_memories_disc_1.jpg', '39febe05-b072-47b6-b0c8-bb745e59b769'),
    ('OTHER', 21, 'https://example.com/random_access_memories_disc_2.jpg', '985c99c9-bbe3-44d1-bad2-d6c86c9d0a6b');

