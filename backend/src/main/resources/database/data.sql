-- Provide SQL scripts here

SET SCHEMA tkm;

INSERT INTO tkm.shops (id, name, address, active)
VALUES (1, 'West Houston Auto Repair', '1950 S Texas 6, Houston, TX 77077, United States', true),
       (2, 'J&T Automotive', '9046 Westview Dr, Houston, TX 77055, United States', true),
       (3, 'Ron''s Downtown Auto Service', '406 Dennis St, Houston, TX 77006, United States', true),
       (4, 'Auto Tek', '4313 Gillis St, Austin, TX 78745, United States', true);