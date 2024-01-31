-- Basic data entity table and related schema

create table if not exists data (
    id      UUID                     not null,

    name    character varying(100)   not null,
    count   integer                  not null,

    created timestamp with time zone not null default CURRENT_TIMESTAMP,
    updated timestamp with time zone not null default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,

    constraint pk_data primary key (id)
);
