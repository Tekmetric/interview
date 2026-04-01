SET @listing1 = random_uuid();
SET @listing2 = random_uuid();
SET @listing3 = random_uuid();
SET @listing4 = random_uuid();
SET @listing5 = random_uuid();

INSERT INTO listings (listing_id, address, agent_name, property_type, listing_price)
VALUES (@listing1, '123 Elm St', 'Bob Ross', 'HOUSE', 500000.0),
       (@listing2, '678 Pine Dr', 'Bill Murry', 'HOUSE', 300000.0),
       (@listing3, '1000 Maple Ct', 'Steve McSteve', 'DUPLEX', 450000.0),
       (@listing4, '500 Ash Pkwy', 'Jill Valentine', 'APARTMENT', 250000.0),
       (@listing5, '777 Oak Pl', 'Steven Colbert', 'TOWNHOUSE', 900000.0);

INSERT INTO offers (offer_id, offer_price, lender_name, status, listing_id)
VALUES (random_uuid(), 490000.0, 'Big Finance', 'PENDING', @listing1),
       (random_uuid(), 485000.0, 'Bigger Finance', 'PENDING', @listing1),
       (random_uuid(), 310000.0, 'Big Finance', 'PENDING', @listing2),
       (random_uuid(), 290000.0, 'Big Finance', 'CANCELED', @listing2),
       (random_uuid(), 450000.0, 'Big Finance', 'ACCEPTED', @listing3),
       (random_uuid(), 440000.0, 'Big Finance', 'CANCELED', @listing3),
       (random_uuid(), 400000.0, 'Big Finance', 'CANCELED', @listing3);