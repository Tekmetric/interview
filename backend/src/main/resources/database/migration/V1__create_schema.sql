CREATE TABLE car
(
    id         serial primary key,
    vin        text    not null,
    make       text    not null,
    model      text    not null,
    model_year integer not null,
    customer   text    not null,
    created_at timestamp,
    updated_at timestamp
);

CREATE TABLE job
(
    id           serial primary key,
    fk_car_id    integer not null,
    status       text,
    scheduled_at timestamp,
    created_at   timestamp,
    updated_at   timestamp,
    foreign key (fk_car_id) references car (id)
);

CREATE TABLE task
(
    id            serial primary key,
    fk_job_id     integer not null,
    status        text,
    title         text,
    type          text,
    description   text,
    mechanic_name text,
    created_at    timestamp,
    updated_at    timestamp,
    foreign key (fk_job_id) references job (id)
)
