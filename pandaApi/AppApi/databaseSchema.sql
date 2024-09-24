CREATE TABLE RedPanda(
    id string primary key,
    hasTracker boolean,
    color varchar(50),
    species int,
    name varchar(100),
    age int
);

CREATE TABLE Sighting(
    id string primary key,
    dateTime varchar(50),
    locationLat varchar(100),
    locationLon varchar(100),
    pandaId string
);