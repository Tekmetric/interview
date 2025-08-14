-- The password is hashed password for the string "password".
-- This is for demo purpose only. In prod, should not have hashed password in migration script.
INSERT INTO customer (id, first_name, last_name, email, password) VALUES
(RANDOM_UUID(), 'Cole', 'Palmer', 'x@example.com', '$2a$10$eaL0IQoCyvofGqC.Fn1z7.Szyh4jXCeHcBvOAkuss6j3Ww8gQMhjm'),
(RANDOM_UUID(), null, 'Becker', 'y@example.com', '$2a$10$eaL0IQoCyvofGqC.Fn1z7.Szyh4jXCeHcBvOAkuss6j3Ww8gQMhjm'),
(RANDOM_UUID(), 'Bukayo', 'Saka', 'z@example.com', '$2a$10$eaL0IQoCyvofGqC.Fn1z7.Szyh4jXCeHcBvOAkuss6j3Ww8gQMhjm'),
(RANDOM_UUID(), 'Mo', 'Salah', 'a@example.com', '$2a$10$eaL0IQoCyvofGqC.Fn1z7.Szyh4jXCeHcBvOAkuss6j3Ww8gQMhjm');