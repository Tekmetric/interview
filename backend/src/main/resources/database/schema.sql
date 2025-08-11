-- =============== USERS ===================
CREATE TABLE IF NOT EXISTS app_user (
                                      id               INT AUTO_INCREMENT PRIMARY KEY,
                                      username         VARCHAR(40)   NOT NULL,
                                      email            VARCHAR(120)  NOT NULL,
                                      full_name        VARCHAR(120)  NOT NULL,
                                      role             VARCHAR(20)   NOT NULL,
                                      status           VARCHAR(20)   NOT NULL,
                                      password_hash    VARCHAR(255),

                                      created_date     TIMESTAMP,
                                      created_by       INT,
                                      updated_date     TIMESTAMP,
                                      updated_by       INT,

                                      version          BIGINT        NOT NULL,

                                      CONSTRAINT uk_user_username UNIQUE (username),
                                      CONSTRAINT uk_user_email    UNIQUE (email),
                                      CONSTRAINT fk_user_created_by FOREIGN KEY (created_by) REFERENCES app_user(id),
                                      CONSTRAINT fk_user_updated_by FOREIGN KEY (updated_by) REFERENCES app_user(id)
);

CREATE INDEX IF NOT EXISTS idx_user_username ON app_user(username);
CREATE INDEX IF NOT EXISTS idx_user_email    ON app_user(email);

-- =============== PLANES ==================
CREATE TABLE IF NOT EXISTS plane (
                                     id                   INT AUTO_INCREMENT PRIMARY KEY,
                                     registration_number  VARCHAR(20)  NOT NULL,
                                     manufacturer         VARCHAR(50)  NOT NULL,
                                     model                VARCHAR(50)  NOT NULL,
                                     seat_capacity        INT          NOT NULL,
                                     range_km             INT          NOT NULL,
                                     status               VARCHAR(20)  NOT NULL,

                                     created_date         TIMESTAMP,
                                     created_by           INT,
                                     updated_date         TIMESTAMP,
                                     updated_by           INT,

                                     version              BIGINT       NOT NULL,

                                     CONSTRAINT uk_plane_registration UNIQUE (registration_number),
                                     CONSTRAINT fk_plane_created_by FOREIGN KEY (created_by) REFERENCES app_user(id),
                                     CONSTRAINT fk_plane_updated_by FOREIGN KEY (updated_by) REFERENCES app_user(id)
);

CREATE INDEX IF NOT EXISTS idx_plane_status ON plane(status);

-- =============== FLIGHTS =================
CREATE TABLE IF NOT EXISTS flight (
                                      id                 INT AUTO_INCREMENT PRIMARY KEY,
                                      code               VARCHAR(10)   NOT NULL,
                                      departure_airport  CHAR(3)       NOT NULL,
                                      arrival_airport    CHAR(3)       NOT NULL,
                                      departure_time     TIMESTAMP     NOT NULL,
                                      arrival_time       TIMESTAMP     NOT NULL,
                                      status             VARCHAR(20)   NOT NULL,
                                      available_seats    INT           NOT NULL,
                                      price              DECIMAL(10,2),
                                      currency           CHAR(3),
                                      terminal           VARCHAR(5),
                                      gate               VARCHAR(5),

                                      plane_id           INT           NOT NULL,

                                      created_date       TIMESTAMP,
                                      created_by         INT,
                                      updated_date       TIMESTAMP,
                                      updated_by         INT,

                                      version            BIGINT        NOT NULL,

                                      CONSTRAINT uk_flight_code UNIQUE (code),
                                      CONSTRAINT fk_flight_plane      FOREIGN KEY (plane_id)   REFERENCES plane(id),
                                      CONSTRAINT fk_flight_created_by FOREIGN KEY (created_by)  REFERENCES app_user(id),
                                      CONSTRAINT fk_flight_updated_by FOREIGN KEY (updated_by)  REFERENCES app_user(id)
);

CREATE INDEX IF NOT EXISTS idx_flight_code           ON flight(code);
CREATE INDEX IF NOT EXISTS idx_flight_departure_time ON flight(departure_time);
CREATE INDEX IF NOT EXISTS idx_flight_plane_id       ON flight(plane_id);