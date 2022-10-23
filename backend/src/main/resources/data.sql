INSERT INTO USER_PROFILE (id, first_name, last_name, phone_number, date_of_birth, created_by, created_date) values (1, 'bogdan', 'popa', '1234567890', '1985-01-25', 'system', current_date)
INSERT INTO USER_PROFILE (id, first_name, last_name, phone_number, date_of_birth, created_by, created_date) values (2, 'bogdan-2', 'popa-2', '1234567891', '1985-01-26', 'system', current_date)
INSERT INTO USER_PROFILE (id, first_name, last_name, phone_number, date_of_birth, created_by, created_date) values (3, 'bogdan-3', 'popa-3', '1234567892', '1985-01-27', 'system', current_date)
INSERT INTO USER_PROFILE (id, first_name, last_name, phone_number, date_of_birth, created_by, created_date) values (4, 'bogdan-4', 'popa-4', '1234567893', '1985-01-28', 'system', current_date)
INSERT INTO USER_PROFILE (id, first_name, last_name, phone_number, date_of_birth, created_by, created_date) values (5, 'bogdan-12', 'popa-122', '1234567893', '1985-01-28', 'system', current_date)
INSERT INTO USER_PROFILE (id, first_name, last_name, phone_number, date_of_birth, created_by, created_date) values (6, 'bogdan-13', 'popa-133', '1234567893', '1985-01-28', 'system', current_date)

INSERT INTO USER (id, email, password, role, created_by, created_date, user_profile_id) values (1, 'bogdanpopa24@gmail.com', 'password', 'ROLE_USER', 'system', current_date, 1)
UPDATE USER_PROFILE SET user_id = 1 where id = 1

INSERT INTO USER (id, email, password, role, created_by, created_date, user_profile_id) values (2, 'bogdanpopa24-1@gmail.com', 'password-1', 'ROLE_USER', 'system', current_date, 2)
UPDATE USER_PROFILE SET user_id = 2 where id = 2

INSERT INTO USER (id, email, password, role, created_by, created_date, user_profile_id) values (3, 'bogdanpopa24-2@gmail.com', 'password-2', 'ROLE_USER', 'system', current_date, 3)
UPDATE USER_PROFILE SET user_id = 3 where id = 3

INSERT INTO USER (id, email, password, role, created_by, created_date, user_profile_id) values (4, 'bogdanpopa24-3@gmail.com', 'password-3', 'ROLE_USER', 'system', current_date, 4)
UPDATE USER_PROFILE SET user_id = 4 where id = 4

INSERT INTO USER (id, email, password, role, created_by, created_date, user_profile_id) values (5, 'bogdanpopa24-12@gmail.com', 'password-12', 'ROLE_USER', 'system', current_date, 5)
UPDATE USER_PROFILE SET user_id = 5 where id = 5

INSERT INTO USER (id, email, password, role, created_by, created_date, user_profile_id) values (6, 'bogdanpopa24-13@gmail.com', 'password-13', 'ROLE_USER', 'system', current_date, 6)
UPDATE USER_PROFILE SET user_id = 6 where id = 6