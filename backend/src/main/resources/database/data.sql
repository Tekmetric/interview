-- Provide SQL scripts here
INSERT INTO users (id, name, organization, administrator) VALUES (NEXT VALUE FOR user_sequence, 'Kelly', 'Tekmetric', true);
INSERT INTO users (id, name, organization, administrator) VALUES (NEXT VALUE FOR user_sequence, 'Gwen', 'Tekmetric', false);
INSERT INTO users (id, name, organization, administrator) VALUES (NEXT VALUE FOR user_sequence, 'Michael', 'Costco', true);
INSERT INTO users (id, name, organization, administrator) VALUES (NEXT VALUE FOR user_sequence, 'Stacy', 'Costco', false);
INSERT INTO users (id, name, organization, administrator) VALUES (NEXT VALUE FOR user_sequence, 'Frank', 'Costco', false);