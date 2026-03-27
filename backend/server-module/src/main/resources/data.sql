----------------------------------------------------------
-- USERS (15 total)
----------------------------------------------------------
SET AUTOCOMMIT TRUE;

INSERT INTO users (id, first_name, last_name, email, birth_date, created_date) VALUES
(RANDOM_UUID(),'User1','Last1','user1.last1@example.com',DATE '1990-01-01',CURRENT_TIMESTAMP),
(RANDOM_UUID(),'User2','Last2','user2.last2@example.com',DATE '1991-02-02',CURRENT_TIMESTAMP),
(RANDOM_UUID(),'User3','Last3','user3.last3@example.com',DATE '1992-03-03',CURRENT_TIMESTAMP),
(RANDOM_UUID(),'User4','Last4','user4.last4@example.com',DATE '1993-04-04',CURRENT_TIMESTAMP),
(RANDOM_UUID(),'User5','Last5','user5.last5@example.com',DATE '1994-05-05',CURRENT_TIMESTAMP),
(RANDOM_UUID(),'User6','Last6','user6.last6@example.com',DATE '1995-06-06',CURRENT_TIMESTAMP),
(RANDOM_UUID(),'User7','Last7','user7.last7@example.com',DATE '1996-07-07',CURRENT_TIMESTAMP),
(RANDOM_UUID(),'User8','Last8','user8.last8@example.com',DATE '1997-08-08',CURRENT_TIMESTAMP),
(RANDOM_UUID(),'User9','Last9','user9.last9@example.com',DATE '1998-09-09',CURRENT_TIMESTAMP),
(RANDOM_UUID(),'User10','Last10','user10.last10@example.com',DATE '1999-10-10',CURRENT_TIMESTAMP),
(RANDOM_UUID(),'User11','Last11','user11.last11@example.com',DATE '1985-01-11',CURRENT_TIMESTAMP),
(RANDOM_UUID(),'User12','Last12','user12.last12@example.com',DATE '1986-02-12',CURRENT_TIMESTAMP),
(RANDOM_UUID(),'User13','Last13','user13.last13@example.com',DATE '1987-03-13',CURRENT_TIMESTAMP),
(RANDOM_UUID(),'User14','Last14','user14.last14@example.com',DATE '1988-04-14',CURRENT_TIMESTAMP),
(RANDOM_UUID(),'User15','Last15','user15.last15@example.com',DATE '1989-05-15',CURRENT_TIMESTAMP);

SET AUTOCOMMIT FALSE;

----------------------------------------------------------
-- Helper SELECT user_id by row index:
-- (SELECT id FROM users LIMIT 1 OFFSET N)
----------------------------------------------------------

----------------------------------------------------------
-- 20 cars assigned to first 10 users (2 each)
----------------------------------------------------------

INSERT INTO cars (id, owner_id, make, model, manufacture_year, color, created_date, updated_date) VALUES (RANDOM_UUID(), (SELECT id FROM users LIMIT 1 OFFSET 0), 'Toyota','RAV4', 2020,'red',   CURRENT_TIMESTAMP,CURRENT_TIMESTAMP);
INSERT INTO cars (id, owner_id, make, model, manufacture_year, color, created_date, updated_date) VALUES (RANDOM_UUID(), (SELECT id FROM users LIMIT 1 OFFSET 0), 'Toyota','Camry',2018,'black', CURRENT_TIMESTAMP,CURRENT_TIMESTAMP);

INSERT INTO cars (id, owner_id, make, model, manufacture_year, color, created_date, updated_date) VALUES (RANDOM_UUID(), (SELECT id FROM users LIMIT 1 OFFSET 1), 'Honda','CR-V',  2019,'white', CURRENT_TIMESTAMP,CURRENT_TIMESTAMP);
INSERT INTO cars (id, owner_id, make, model, manufacture_year, color, created_date, updated_date) VALUES (RANDOM_UUID(), (SELECT id FROM users LIMIT 1 OFFSET 1), 'Honda','Accord',2021,'blue',  CURRENT_TIMESTAMP,CURRENT_TIMESTAMP);

INSERT INTO cars (id, owner_id, make, model, manufacture_year, color, created_date, updated_date) VALUES (RANDOM_UUID(), (SELECT id FROM users LIMIT 1 OFFSET 2), 'BMW','i7',    2023,'black', CURRENT_TIMESTAMP,CURRENT_TIMESTAMP);
INSERT INTO cars (id, owner_id, make, model, manufacture_year, color, created_date, updated_date) VALUES (RANDOM_UUID(), (SELECT id FROM users LIMIT 1 OFFSET 2), 'BMW','X5',    2017,'white', CURRENT_TIMESTAMP,CURRENT_TIMESTAMP);

INSERT INTO cars (id, owner_id, make, model, manufacture_year, color, created_date, updated_date) VALUES (RANDOM_UUID(), (SELECT id FROM users LIMIT 1 OFFSET 3), 'Ford','Escape',2015,'red',   CURRENT_TIMESTAMP,CURRENT_TIMESTAMP);
INSERT INTO cars (id, owner_id, make, model, manufacture_year, color, created_date, updated_date) VALUES (RANDOM_UUID(), (SELECT id FROM users LIMIT 1 OFFSET 3), 'Ford','F-150', 2016,'black', CURRENT_TIMESTAMP,CURRENT_TIMESTAMP);

INSERT INTO cars (id, owner_id, make, model, manufacture_year, color, created_date, updated_date) VALUES (RANDOM_UUID(), (SELECT id FROM users LIMIT 1 OFFSET 4), 'Tesla','Y',   2022,'blue',  CURRENT_TIMESTAMP,CURRENT_TIMESTAMP);
INSERT INTO cars (id, owner_id, make, model, manufacture_year, color, created_date, updated_date) VALUES (RANDOM_UUID(), (SELECT id FROM users LIMIT 1 OFFSET 4), 'Tesla','3',   2020,'white', CURRENT_TIMESTAMP,CURRENT_TIMESTAMP);

INSERT INTO cars (id, owner_id, make, model, manufacture_year, color, created_date, updated_date) VALUES (RANDOM_UUID(), (SELECT id FROM users LIMIT 1 OFFSET 5), 'Audi','Q5',   2018,'black', CURRENT_TIMESTAMP,CURRENT_TIMESTAMP);
INSERT INTO cars (id, owner_id, make, model, manufacture_year, color, created_date, updated_date) VALUES (RANDOM_UUID(), (SELECT id FROM users LIMIT 1 OFFSET 5), 'Audi','A7',   2024,'red',   CURRENT_TIMESTAMP,CURRENT_TIMESTAMP);

INSERT INTO cars (id, owner_id, make, model, manufacture_year, color, created_date, updated_date) VALUES (RANDOM_UUID(), (SELECT id FROM users LIMIT 1 OFFSET 6), 'Toyota','RAV4',2014,'white', CURRENT_TIMESTAMP,CURRENT_TIMESTAMP);
INSERT INTO cars (id, owner_id, make, model, manufacture_year, color, created_date, updated_date) VALUES (RANDOM_UUID(), (SELECT id FROM users LIMIT 1 OFFSET 6), 'Honda','CR-V', 2023,'blue',  CURRENT_TIMESTAMP,CURRENT_TIMESTAMP);

INSERT INTO cars (id, owner_id, make, model, manufacture_year, color, created_date, updated_date) VALUES (RANDOM_UUID(), (SELECT id FROM users LIMIT 1 OFFSET 7), 'BMW','X5',   2019,'red',   CURRENT_TIMESTAMP,CURRENT_TIMESTAMP);
INSERT INTO cars (id, owner_id, make, model, manufacture_year, color, created_date, updated_date) VALUES (RANDOM_UUID(), (SELECT id FROM users LIMIT 1 OFFSET 7), 'Audi','A7',  2016,'black', CURRENT_TIMESTAMP,CURRENT_TIMESTAMP);

INSERT INTO cars (id, owner_id, make, model, manufacture_year, color, created_date, updated_date) VALUES (RANDOM_UUID(), (SELECT id FROM users LIMIT 1 OFFSET 8), 'Ford','F-150',2011,'white', CURRENT_TIMESTAMP,CURRENT_TIMESTAMP);
INSERT INTO cars (id, owner_id, make, model, manufacture_year, color, created_date, updated_date) VALUES (RANDOM_UUID(), (SELECT id FROM users LIMIT 1 OFFSET 8), 'Tesla','3',   2013,'blue',  CURRENT_TIMESTAMP,CURRENT_TIMESTAMP);

INSERT INTO cars (id, owner_id, make, model, manufacture_year, color, created_date, updated_date) VALUES (RANDOM_UUID(), (SELECT id FROM users LIMIT 1 OFFSET 9), 'Audi','Q5',  2009,'red',   CURRENT_TIMESTAMP,CURRENT_TIMESTAMP);
INSERT INTO cars (id, owner_id, make, model, manufacture_year, color, created_date, updated_date) VALUES (RANDOM_UUID(), (SELECT id FROM users LIMIT 1 OFFSET 9), 'Toyota','Camry',2024,'white',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP);

----------------------------------------------------------
-- Next 5 users get 1 car each (5 cars)
----------------------------------------------------------

INSERT INTO cars (id, owner_id, make, model, manufacture_year, color, created_date, updated_date) VALUES (RANDOM_UUID(),(SELECT id FROM users LIMIT 1 OFFSET 10),'BMW','i7',    2018,'blue', CURRENT_TIMESTAMP,CURRENT_TIMESTAMP);
INSERT INTO cars (id, owner_id, make, model, manufacture_year, color, created_date, updated_date) VALUES (RANDOM_UUID(),(SELECT id FROM users LIMIT 1 OFFSET 11),'Ford','Escape',2020,'black',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP);
INSERT INTO cars (id, owner_id, make, model, manufacture_year, color, created_date, updated_date) VALUES (RANDOM_UUID(),(SELECT id FROM users LIMIT 1 OFFSET 12),'Toyota','RAV4',2012,'white',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP);
INSERT INTO cars (id, owner_id, make, model, manufacture_year, color, created_date, updated_date) VALUES (RANDOM_UUID(),(SELECT id FROM users LIMIT 1 OFFSET 13),'Tesla','Y',  2023,'red',  CURRENT_TIMESTAMP,CURRENT_TIMESTAMP);
INSERT INTO cars (id, owner_id, make, model, manufacture_year, color, created_date, updated_date) VALUES (RANDOM_UUID(),(SELECT id FROM users LIMIT 1 OFFSET 14),'Honda','Accord',2017,'black',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP);

----------------------------------------------------------
-- Remaining 25 cars without owners — VALID MAKE↔MODEL PAIRS
----------------------------------------------------------

-- 4 base samples:
INSERT INTO cars (id, owner_id, make, model, manufacture_year, color, created_date, updated_date) VALUES (RANDOM_UUID(),NULL,'Audi','Q5',   2004,'white',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP);
INSERT INTO cars (id, owner_id, make, model, manufacture_year, color, created_date, updated_date) VALUES (RANDOM_UUID(),NULL,'Ford','F-150',2010,'red',  CURRENT_TIMESTAMP,CURRENT_TIMESTAMP);
INSERT INTO cars (id, owner_id, make, model, manufacture_year, color, created_date, updated_date) VALUES (RANDOM_UUID(),NULL,'Honda','CR-V',2015,'blue', CURRENT_TIMESTAMP,CURRENT_TIMESTAMP);
INSERT INTO cars (id, owner_id, make, model, manufacture_year, color, created_date, updated_date) VALUES (RANDOM_UUID(),NULL,'Tesla','3',   2018,'black',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP);

-- Generate remaining 21 using correct model-per-make mapping
INSERT INTO cars (id, owner_id, make, model, manufacture_year, color, created_date, updated_date)
SELECT RANDOM_UUID(),
       NULL,
       make,
       CASE make
            WHEN 'Toyota' THEN (ARRAY['RAV4','Camry'])[MOD(x,2)+1]
            WHEN 'Honda'  THEN (ARRAY['CR-V','Accord'])[MOD(x,2)+1]
            WHEN 'BMW'    THEN (ARRAY['i7','X5'])[MOD(x,2)+1]
            WHEN 'Ford'   THEN (ARRAY['Escape','F-150'])[MOD(x,2)+1]
            WHEN 'Tesla'  THEN (ARRAY['Y','3'])[MOD(x,2)+1]
            WHEN 'Audi'   THEN (ARRAY['Q5','A7'])[MOD(x,2)+1]
       END as model,
       2000 + MOD(x,26),
       (ARRAY['red','black','white','blue'])[MOD(x,4)+1],
       CURRENT_TIMESTAMP,
       CURRENT_TIMESTAMP
FROM (
       SELECT
         x,
         (ARRAY['Toyota','Honda','BMW','Ford','Tesla','Audi'])[MOD(x,6)+1] AS make
       FROM SYSTEM_RANGE(1,21)
     );
