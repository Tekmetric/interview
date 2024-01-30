drop table users if exists;
drop table vehicles if exists;
drop sequence if exists hibernate_sequence;

create sequence hibernate_sequence start with 1 increment by 1;

create table users (
    id                  bigint not null,
    email               varchar(255) not null,
    first_name          varchar(200) not null,
    last_name           varchar(200) not null,
    password            varchar(255) not null,
    role                varchar(255) not null,
    primary key (id)
);

create table vehicles (
    id                  bigint not null,
    brand               varchar(255),
    cost                double not null,
    created_at          timestamp not null,
    deleted_at          timestamp,
    license_plate       varchar(10) not null,
    model               varchar(255),
    registration_year   integer,
    state               varchar(255) not null,
    updated_at          timestamp not null,
    primary key (id)
);

alter table users
    add constraint UK_user_email unique (email);

alter table vehicles
    add constraint UK_vehicle_license_plate unique (license_plate);