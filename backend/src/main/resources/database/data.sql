create table league (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name varchar(255) not null,
    location varchar(255) not null,
    skill_level varchar(255) not null
);

create table team (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name varchar(255) not null,
    players varchar(255) not null,
    league_id BIGINT,
    foreign key (league_id) references league(id)
)
