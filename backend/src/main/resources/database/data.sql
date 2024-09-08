-- Provide SQL scripts here

-- SQL script to pre-populate data in the db
-- The insert statements for cars assume that the database has generated client ids starting from 1, incremented by 1
-- Similarly, insert statements from service requests assume that the database has generated car ids starting from 1, incremented by 1
-- In a real system, we would only use APIs to insert these rows and would use the ids as references in car and service request creations subsequently as needed

-- clients
INSERT INTO clients
(name, phone, address, email)
VALUES('Mukta Joglekar', '1111111111', 'address line 1, city, Virginia, zip', 'example@email.com');

INSERT INTO clients
(name, phone, address, email)
VALUES('Michael Phelps', '1111111112', '1 swimming lane, Sydney, Virginia, zip', 'mphelps@email.com');

INSERT INTO clients
(name, phone, address, email)
VALUES('Katie Ledecki', '1111111113', '2 swimming lane, Paris, Maryland, zip', 'kledecki@email.com');

INSERT INTO clients
(name, phone, address, email)
VALUES('Simone Biles', '1111111114', '3 Gym lane, Greece, Pennsylvania, zip', 'sbiles@email.com');

INSERT INTO clients
(name, phone, address, email)
VALUES('Manu Bhaker', '1111111115', '4 Deccan Dr, New Delhi, New Jersey, zip', 'mbhaker@email.com');

INSERT INTO clients
(name, phone, address, email)
VALUES('Lakshya Sen', '1111111116', '4 Badminton Place, Pune, North Carolina, zip', 'lsen@email.com');


-- cars

INSERT INTO cars
(vin, make, model, color, license_plate, owner_id)
VALUES('232323232', 'Honda', 'Civic', 'Blue', 'mukta', 1);

INSERT INTO cars
(vin, make, model, color, license_plate, owner_id)
VALUES('2323232323', 'Honda', 'CRV', 'Grey', 'michael', 2);

INSERT INTO cars
(vin, make, model, color, license_plate, owner_id)
VALUES('2323232323', 'Subaru', 'Outback', 'Purple', 'michael2', 2);


INSERT INTO cars
(vin, make, model, color, license_plate, owner_id)
VALUES('23232323232', 'Nissan', 'Sentra', 'Red', 'katie', 3);

INSERT INTO cars
(vin, make, model, color, license_plate, owner_id)
VALUES('232323232323', 'Toyota', 'Corolla', 'Blue', 'simone', 4);


-- service requests
INSERT INTO service_requests
(car_id, work, status, creation_date, estimated_completion_time, completion_time, estimated_charge, charge)
VALUES( 1, 'Oil change', 'completed', {ts '2024-07-27 8:30:52.69'}, {ts '2024-07-17 10:30:52.69'}, {ts '2024-07-27 9:30:52.69'}, 50.00, 50.00);


INSERT INTO service_requests
(car_id, work, status, creation_date, estimated_completion_time, completion_time, estimated_charge, charge)
VALUES( 2, 'Tire Rotation', 'completed', {ts '2024-07-28 10:00:52.69'}, {ts '2024-07-28 12:20:52.69'}, {ts '2024-07-28 13:30:52.69'}, 80.00, 50.00);


INSERT INTO service_requests
(car_id, work, status, creation_date, estimated_completion_time, completion_time, estimated_charge, charge)
VALUES( 2, 'Replacing tires', 'in_review', {ts '2024-07-29 11:30:52.69'}, {ts '2024-07-17 18:30:52.69'}, null, 500.00, null);


INSERT INTO service_requests
(car_id, work, status, creation_date, estimated_completion_time, completion_time, estimated_charge, charge)
VALUES( 3, 'Replacing Battery', 'in_progress', {ts '2024-07-17 8:30:52.69'}, {ts '2024-07-17 8:30:52.69'}, null, 1000.00, null);

INSERT INTO service_requests
(car_id, work, status, creation_date, estimated_completion_time, completion_time, estimated_charge, charge)
VALUES( 4, 'Oil change', 'not_started', {ts '2024-07-29 8:30:52.69'}, {ts '2024-08-03 12:30:52.69'}, null, 50.00, null);
