-- Provide SQL scripts here
DROP TABLE IF EXISTS customer;

CREATE TABLE customer (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          email VARCHAR(100) UNIQUE,
                          first_name VARCHAR(255),
                          last_name VARCHAR(255),
                          address VARCHAR(1000)
);

INSERT INTO customer (email, first_name, last_name) VALUES
                                                        ( 'john.doe@example.com', 'John', 'Doe'),
                                                        ( 'foo.bar@example.com' , 'Taylor', 'Swift'),
                                                        ( 'annemiller@str.com', 'Anne', 'Miller'),
                                                        ( 'bart@example.com' , 'Bart', 'Simpson');