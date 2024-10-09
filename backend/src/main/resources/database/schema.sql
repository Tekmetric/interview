-- create table `cats`
CREATE TABLE cats (
   id               BIGINT      AUTO_INCREMENT  PRIMARY KEY,
   name             VARCHAR(10)                 NOT NULL,
   age              INT                         NOT NULL,
   fur_color        VARCHAR(25)                 NOT NULL,
   tag_line         VARCHAR(100)
);
