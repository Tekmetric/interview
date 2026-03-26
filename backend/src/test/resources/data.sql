-- test/resources/data.sql
INSERT INTO api_key (id, name, api_key, active) VALUES
    (RANDOM_UUID(), 'int-test-client', 'skey_inttest1234', TRUE);