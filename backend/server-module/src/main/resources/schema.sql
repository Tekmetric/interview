drop table if exists cars cascade;
drop table if exists users cascade;

CREATE TABLE users (
    id UUID PRIMARY KEY,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    email VARCHAR(200),
    birth_date DATE,
    created_date TIMESTAMP,
    updated_date TIMESTAMP
);

CREATE TABLE cars (
    id UUID PRIMARY KEY,
    make VARCHAR(100),
    model VARCHAR(100),
    manufacture_year INT,
    color VARCHAR(50),
    owner_id UUID,
    created_date TIMESTAMP,
    updated_date TIMESTAMP,
    CONSTRAINT fk_car_user FOREIGN KEY (owner_id) REFERENCES users(id)
);


create index idx_cars_owner on cars (owner_id);