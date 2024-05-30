-- Provide SQL scripts here

-- Schema creation
-- Note: schema must be altered since there is a bug in H2
-- that prevents auto-incrementing integers for the database when
-- the JPA entity ID is null.

-- https://github.com/h2database/h2database/issues/3231
alter table ingredients alter column id set default on null;
alter table meal alter column id set default on null;
alter table "user" alter column id set default on null;

-- Data loading

insert into "user" (username, first, last) values ('johndoe', 'John', 'Doe');
commit;

insert into meal (FK_USER, NAME) values (1, 'Pizza'); -- 1
commit;

insert into ingredients (FK_MEAL, NAME, QUANTITY, UNITS) values (1, 'Tomato Sauce', 4.0, 'oz');
insert into ingredients (FK_MEAL, NAME, QUANTITY, UNITS) values (1, 'Mozarrella Cheese', 8.0, 'oz');
insert into ingredients (FK_MEAL, NAME, QUANTITY, UNITS) values (1, 'Pepperoni', 0.5, 'lbs');
insert into ingredients (FK_MEAL, NAME, QUANTITY, UNITS) values (1, 'Large Pizza Crust', 250, 'g');

commit;