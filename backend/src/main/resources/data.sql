-- Provide SQL scripts here
-- Iniitalizing Books DB with three example books
CREATE TABLE Books(ID INT PRIMARY KEY NOT NULL AUTO_INCREMENT, Title VARCHAR(255), Author VARCHAR(255), PublishYear INT);
INSERT INTO Books(Title, Author, PublishYear) VALUES('Children of Time', 'Adrian Tchaikovsky', 2015);
INSERT INTO Books(Title, Author, PublishYear) VALUES('I Am a Strange Loop', 'Douglas Hofstadter', 2007);
INSERT INTO Books(Title, Author, PublishYear) VALUES('Murtagh', 'Christopher Paolini', 2023);
