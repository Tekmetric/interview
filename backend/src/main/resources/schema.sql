DROP TABLE IF EXISTS movie_actor;

DROP TABLE IF EXISTS movie_keyword;

DROP TABLE IF EXISTS movie;

DROP TABLE IF EXISTS director;

DROP TABLE IF EXISTS actor;

DROP TABLE IF EXISTS keyword;

CREATE TABLE
    director (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        first_name VARCHAR(255) NOT NULL,
        last_name VARCHAR(255) NOT NULL,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        CONSTRAINT unique_first_last_name_director UNIQUE (first_name, last_name)
    );

CREATE TABLE
    actor (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        first_name VARCHAR(255) NOT NULL,
        last_name VARCHAR(255) NOT NULL,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        CONSTRAINT unique_first_last_name_actor UNIQUE (first_name, last_name)
    );

CREATE TABLE
    movie (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        title VARCHAR(255) NOT NULL UNIQUE,
        genre VARCHAR(255) NOT NULL,
        rating DECIMAL(3, 1),
        release_year INT NOT NULL,
        duration INT NOT NULL,
        director_id INT NOT NULL,
        description TEXT,
        language VARCHAR(255),
        budget BIGINT,
        box_office BIGINT,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        FOREIGN KEY (director_id) REFERENCES director (id)
    );

CREATE TABLE
    movie_actor (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        movie_id INT NOT NULL,
        actor_id INT NOT NULL,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        FOREIGN KEY (movie_id) REFERENCES movie (id),
        FOREIGN KEY (actor_id) REFERENCES actor (id)
    );

CREATE TABLE
    keyword (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        name VARCHAR(255) NOT NULL UNIQUE
    );

CREATE TABLE
    movie_keyword (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        movie_id INT NOT NULL,
        keyword_id INT NOT NULL,
        FOREIGN KEY (movie_id) REFERENCES movie (id) ON DELETE CASCADE,
        FOREIGN KEY (keyword_id) REFERENCES keyword (id) ON DELETE CASCADE
    );