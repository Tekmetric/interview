INSERT INTO SURFACE (id, NAME)
VALUES (1, 'HARD');
INSERT INTO SURFACE (id, NAME)
VALUES (2, 'CLAY');
INSERT INTO SURFACE (id, NAME)
VALUES (3, 'GRASS');
INSERT INTO SURFACE (id, NAME)
VALUES (4, 'ARTIFICIAL');

INSERT INTO TOURNAMENT (NAME, CITY, COUNTRY, PRIZE_MONEY, DATE, SURFACE_ID)
VALUES ('US Open', 'New York', 'USA', 1500000, '2023-10-09', 1);
INSERT INTO TOURNAMENT (NAME, CITY, COUNTRY, PRIZE_MONEY, DATE, SURFACE_ID)
VALUES ('Wimbledon', 'London', 'UK', 1500000, '2023-10-07', 3);

INSERT INTO RACQUET (BRAND, MODEL, WEIGHT, HEAD_SIZE)
VALUES ('Babolat', 'Pure Aero', 300, 100);
INSERT INTO RACQUET (BRAND, MODEL, WEIGHT, HEAD_SIZE)
VALUES ('Head', 'Speed', 310, 98);

INSERT INTO STATS (ACES, DOUBLE_FAULTS, WINS, LOSSES, TOURNAMENTS_PLAYED)
VALUES (4, 2, 4, 1, 2);
INSERT INTO STATS (ACES, DOUBLE_FAULTS, WINS, LOSSES, TOURNAMENTS_PLAYED)
VALUES (7, 3, 9, 4, 4);

INSERT INTO PLAYER (NAME, RANK, BIRTHDATE, BIRTHPLACE, TURNED_PRO, WEIGHT, HEIGHT, COACH, STATS_ID)
VALUES ('Alin Bizau', 1, '1990-06-22', 'Romania', '2009-06-11', 71, 178, 'Roger Federer', 1);
INSERT INTO PLAYER (NAME, RANK, BIRTHDATE, BIRTHPLACE, TURNED_PRO, WEIGHT, HEIGHT, COACH, STATS_ID)
VALUES ('Cristina Bizau', 2, '1990-11-11', 'Romania', '2012-06-26', 59, 171, 'Rafael Nadal', 2);

INSERT INTO SCORE (OPPONENT_NAME, POINTS, OPPONENT_POINTS)
VALUES ('Carlos Alcaraz', 1, 6);
INSERT INTO SCORE (OPPONENT_NAME, POINTS, OPPONENT_POINTS)
VALUES ('Holger Rune', 4, 6);

INSERT INTO PLAYER_PREVIOUS_RESULTS (PLAYER_ID, PREVIOUS_RESULTS_ID)
VALUES (1, 1);
INSERT INTO PLAYER_PREVIOUS_RESULTS (PLAYER_ID, PREVIOUS_RESULTS_ID)
VALUES (2, 2);

INSERT INTO PLAYER_RACQUET (PLAYER_ID, RACQUET_ID)
VALUES (1, 1);
INSERT INTO PLAYER_RACQUET (PLAYER_ID, RACQUET_ID)
VALUES (1, 2);
INSERT INTO PLAYER_RACQUET (PLAYER_ID, RACQUET_ID)
VALUES (2, 1);

INSERT INTO PLAYER_TOURNAMENT (PLAYER_ID, TOURNAMENT_ID)
VALUES (1, 1);
INSERT INTO PLAYER_TOURNAMENT (PLAYER_ID, TOURNAMENT_ID)
VALUES (1, 2);
INSERT INTO PLAYER_TOURNAMENT (PLAYER_ID, TOURNAMENT_ID)
VALUES (2, 1);
