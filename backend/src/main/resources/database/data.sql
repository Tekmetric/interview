create table inventory
(
    id          bigint       not null
        constraint inventory_pkey primary key,
    type        varchar(64)  not null,
    status      varchar(64)  not null,
    brand       varchar(100) not null,
    part_name   varchar(100) not null,
    part_number varchar(100) not null,
    quantity    int          not null,
    support_email varchar(100) not null,
    created_at  timestamp    not null default CURRENT_TIMESTAMP,
    updated_at  bigint not null default 0,
    deleted_at timestamp
);

CREATE
INDEX idx_brand_name ON Inventory (brand);
CREATE
INDEX idx_part_name ON Inventory (part_name);
CREATE
INDEX idx_part_number ON Inventory (part_number);

