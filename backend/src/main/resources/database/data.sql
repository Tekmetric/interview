-- Insert default admin account
-- Password: password123 (BCrypt encoded)
INSERT INTO account (id, first_name, last_name, email, password, phone, created_at, updated_at) VALUES
(RANDOM_UUID(), 'Lingchao', 'Kong', 'admin@example.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', '123-456-7890', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

-- Insert sample events (table created by Hibernate)
INSERT INTO event (id, name, description, event_date, location, created_at, updated_at) VALUES
-- Tech Events
(RANDOM_UUID(), 'Spring Boot Workshop', 'Learn the fundamentals of Spring Boot framework', '2025-11-15 10:00:00', 'San Francisco, CA', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
(RANDOM_UUID(), 'Java Conference 2025', 'Annual Java developers conference with industry experts', '2025-12-01 09:00:00', 'New York, NY', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
(RANDOM_UUID(), 'Microservices Meetup', 'Discussion on microservices architecture patterns', '2025-11-20 18:30:00', 'Austin, TX', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),

-- Concerts
(RANDOM_UUID(), 'Taylor Swift - The Eras Tour', 'Experience the magic of Taylor Swift performing hits from all her albums', '2025-11-28 19:30:00', 'MetLife Stadium, East Rutherford, NJ', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
(RANDOM_UUID(), 'Taylor Swift - The Eras Tour', 'Taylor Swift live in concert with special guests', '2025-12-05 19:30:00', 'SoFi Stadium, Los Angeles, CA', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
(RANDOM_UUID(), 'Beyoncé - Renaissance World Tour', 'Queen Bey brings the Renaissance experience to life', '2025-12-10 20:00:00', 'AT&T Stadium, Arlington, TX', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
(RANDOM_UUID(), 'Ed Sheeran - Mathematics Tour', 'Ed Sheeran performs acoustic and electric hits', '2025-11-25 19:00:00', 'Madison Square Garden, New York, NY', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
(RANDOM_UUID(), 'The Weeknd - After Hours Tour', 'The Weeknd live with stunning visual effects', '2025-12-08 20:00:00', 'United Center, Chicago, IL', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),

-- NBA Games
(RANDOM_UUID(), 'NBA: Lakers vs Warriors', 'Los Angeles Lakers take on Golden State Warriors in Western Conference showdown', '2025-11-22 19:30:00', 'Crypto.com Arena, Los Angeles, CA', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
(RANDOM_UUID(), 'NBA: Celtics vs Heat', 'Boston Celtics vs Miami Heat - Eastern Conference rivalry game', '2025-11-26 20:00:00', 'TD Garden, Boston, MA', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
(RANDOM_UUID(), 'NBA: Nets vs Knicks', 'Brooklyn Nets vs New York Knicks - Battle for New York', '2025-12-03 19:00:00', 'Barclays Center, Brooklyn, NY', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
(RANDOM_UUID(), 'NBA: Mavericks vs Suns', 'Dallas Mavericks vs Phoenix Suns featuring Luka Doncic', '2025-12-07 20:30:00', 'American Airlines Center, Dallas, TX', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
(RANDOM_UUID(), 'NBA: Bucks vs 76ers', 'Milwaukee Bucks vs Philadelphia 76ers - Championship contenders clash', '2025-12-12 19:30:00', 'Fiserv Forum, Milwaukee, WI', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),

-- NFL Games
(RANDOM_UUID(), 'NFL: Chiefs vs Bills', 'Kansas City Chiefs vs Buffalo Bills - AFC Championship rematch', '2025-11-24 16:25:00', 'Arrowhead Stadium, Kansas City, MO', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
(RANDOM_UUID(), 'NFL: Cowboys vs Eagles', 'Dallas Cowboys vs Philadelphia Eagles - NFC East rivalry', '2025-12-01 13:00:00', 'AT&T Stadium, Arlington, TX', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),

-- Soccer
(RANDOM_UUID(), 'MLS Cup Final 2025', 'Major League Soccer Championship Final', '2025-12-09 15:00:00', 'BMO Stadium, Los Angeles, CA', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),

-- Other Sports
(RANDOM_UUID(), 'UFC 300', 'Ultimate Fighting Championship - Stacked fight card', '2025-11-30 22:00:00', 'T-Mobile Arena, Las Vegas, NV', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());