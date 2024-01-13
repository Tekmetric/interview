-- Provide SQL scripts here
create table products if not exists (
    id uuid primary key not null,
    name varchar not null,
    currency varchar(3) not null,
    price numeric(15, 3) not null
    quantity int nullable
)