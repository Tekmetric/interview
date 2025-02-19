INSERT INTO
    director (first_name, last_name)
VALUES
    ('Christopher', 'Nolan'),
    ('Quentin', 'Tarantino'),
    ('Martin', 'Scorsese'),
    ('Steven', 'Spielberg'),
    ('James', 'Cameron'),
    ('Francis Ford', 'Coppola'),
    ('Ridley', 'Scott'),
    ('Peter', 'Jackson'),
    ('David', 'Fincher'),
    ('Denis', 'Villeneuve');

INSERT INTO
    movie (
        title,
        genre,
        rating,
        release_year,
        duration,
        director_id,
        description,
        language,
        budget,
        box_office
    )
VALUES
    (
        'Inception',
        'Sci-Fi',
        8.8,
        2010,
        148,
        1,
        'A thief enters people''s dreams to steal secrets.',
        'English',
        160000000,
        829895144
    ),
    (
        'Pulp Fiction',
        'Crime',
        8.9,
        1994,
        154,
        2,
        'The lives of two mob hitmen, a boxer, and others intertwine.',
        'English',
        8000000,
        214000000
    ),
    (
        'The Wolf of Wall Street',
        'Biography',
        8.2,
        2013,
        180,
        3,
        'A stockbroker rises and falls in a world of excess.',
        'English',
        100000000,
        392000000
    ),
    (
        'Jurassic Park',
        'Adventure',
        8.1,
        1993,
        127,
        4,
        'A dinosaur park goes out of control.',
        'English',
        63000000,
        1030000000
    ),
    (
        'Avatar',
        'Sci-Fi',
        7.8,
        2009,
        162,
        5,
        'A marine on an alien planet struggles between two worlds.',
        'English',
        237000000,
        2847246203
    ),
    (
        'The Godfather',
        'Crime',
        9.2,
        1972,
        175,
        6,
        'The aging patriarch of a crime dynasty transfers control to his son.',
        'English',
        6000000,
        250000000
    ),
    (
        'Gladiator',
        'Action',
        8.5,
        2000,
        155,
        7,
        'A betrayed Roman general fights for vengeance.',
        'English',
        103000000,
        460583960
    ),
    (
        'The Lord of the Rings: The Fellowship of the Ring',
        'Fantasy',
        8.8,
        2001,
        178,
        8,
        'A hobbit sets out on a quest to destroy a powerful ring.',
        'English',
        93000000,
        897000000
    ),
    (
        'Fight Club',
        'Drama',
        8.8,
        1999,
        139,
        9,
        'An insomniac office worker forms an underground fight club.',
        'English',
        63000000,
        101200000
    ),
    (
        'Dune',
        'Sci-Fi',
        8.3,
        2021,
        155,
        10,
        'A noble family gets entangled in a war for desert planet resources.',
        'English',
        165000000,
        433000000
    );

INSERT INTO
    actor (first_name, last_name)
VALUES
    ('Leonardo', 'DiCaprio'),
    ('Samuel L.', 'Jackson'),
    ('John', 'Travolta'),
    ('Morgan', 'Freeman'),
    ('Tom', 'Hanks'),
    ('Marlon', 'Brando'),
    ('Al', 'Pacino'),
    ('Russell', 'Crowe'),
    ('Elijah', 'Wood'),
    ('Brad', 'Pitt'),
    ('Timothée', 'Chalamet'),
    ('Zendaya', 'Coleman');

INSERT INTO
    movie_actor (movie_id, actor_id)
VALUES
    (1, 1),
    (2, 2),
    (2, 3),
    (3, 1),
    (4, 4),
    (5, 5),
    (6, 6),
    (6, 7),
    (7, 8),
    (8, 9),
    (9, 10),
    (10, 11),
    (10, 12);

INSERT INTO
    keyword (name)
VALUES
    ('dreams'),
    ('gangsters'),
    ('stock market'),
    ('dinosaurs'),
    ('alien world'),
    ('mafia'),
    ('revenge'),
    ('fantasy world'),
    ('mind games'),
    ('desert planet');

INSERT INTO
    movie_keyword (movie_id, keyword_id)
VALUES
    (1, 1),
    (2, 2),
    (3, 3),
    (4, 4),
    (5, 5),
    (6, 6),
    (7, 7),
    (8, 8),
    (9, 9),
    (10, 10);