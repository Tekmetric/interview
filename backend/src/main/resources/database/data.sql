-- Provide SQL scripts here
 create table car (id integer not null, color integer, license varchar(255), make integer, model integer, production_year integer, primary key (id));

insert into car (id, make, model, production_year, color, license) values (1, 0, 0, 2008, 0, 'XYZ1234')
insert into car (id, make, model, production_year, color, license) values (2, 1, 2, 2020, 0, 'ABC1234')