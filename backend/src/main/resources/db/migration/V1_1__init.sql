create table car(
    id           uuid primary key,
    color        varchar(512),
    created_at   timestamp(6) with time zone,
    drivetrain   varchar(255),
    make         varchar(512),
    mileage      integer,
    model        varchar(512),
    modified_at  timestamp(6) with time zone,
    transmission varchar(255),
    trim         varchar(512),
    vin          varchar(512),
    model_year   integer
);

alter table car
add constraint uk_vin unique (vin);
