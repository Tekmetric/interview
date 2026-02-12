-- Create indexes for frequently queried fields to improve performance
CREATE INDEX IF NOT EXISTS idx_athlete_nationality ON athlete(nationality);
CREATE INDEX IF NOT EXISTS idx_athlete_discipline ON athlete(discipline);
CREATE INDEX IF NOT EXISTS idx_athlete_duplicate_check ON athlete(first_name, last_name, birth_timestamp);

-- Insert sample athlete data
INSERT INTO athlete (first_name, last_name, birth_timestamp, nationality, discipline, personal_best, bio) VALUES
-- Sprinters
('Usain', 'Bolt', 524966400000, 'Jamaica', '100m', '9.58s', 'Olympic gold medalist and world record holder in 100m and 200m'),
('Shelly-Ann', 'Fraser-Pryce', 599616000000, 'Jamaica', '100m', '10.60s', 'One of the fastest women in history with multiple Olympic golds'),
('Florence', 'Griffith-Joyner', 281664000000, 'USA', '100m', '10.49s', 'World record holder in 100m and 200m for women'),
('Carl', 'Lewis', 110246400000, 'USA', '100m', '9.86s', 'Nine-time Olympic gold medalist in sprints and long jump'),
('Yohan', 'Blake', 607910400000, 'Jamaica', '100m', '9.69s', 'Second fastest man in history in the 100m'),
('Allyson', 'Felix', 520128000000, 'USA', '200m', '21.69s', 'Most decorated female track and field athlete in Olympic history'),

-- Distance Runners
('Mo', 'Farah', 567993600000, 'UK', '5000m', '12:53.11', 'Multiple Olympic and World Championship titles in distance running'),
('Eliud', 'Kipchoge', 478915200000, 'Kenya', 'Marathon', '2:01:09', 'Marathon world record holder and first sub-2-hour marathon'),
('Haile', 'Gebrselassie', 133315200000, 'Ethiopia', '10000m', '26:17.53', 'Former world record holder in multiple distance events'),
('Kenenisa', 'Bekele', 707875200000, 'Ethiopia', '5000m', '12:37.35', 'World record holder in 5000m and 10000m'),
('Paula', 'Radcliffe', 450316800000, 'UK', 'Marathon', '2:15:25', 'Women''s marathon world record holder'),
('Brigid', 'Kosgei', 791337600000, 'Kenya', 'Marathon', '2:14:04', 'Second fastest female marathoner in history'),

-- Middle Distance
('David', 'Rudisha', 536371200000, 'Kenya', '800m', '1:40.91', 'World record holder in 800m with dominant Olympic performances'),
('Caster', 'Semenya', 673488000000, 'South Africa', '800m', '1:54.25', 'Multiple Olympic and World Championship gold medalist'),
('Hicham', 'El Guerrouj', 426384000000, 'Morocco', '1500m', '3:26.00', 'World record holder in 1500m and mile'),

-- Field Events
('Neeraj', 'Chopra', 871603200000, 'India', 'Javelin', '89.94m', 'Olympic gold medalist and Asian record holder in javelin'),
('Mondo', 'Duplantis', 941673600000, 'Sweden', 'Pole Vault', '6.24m', 'World record holder in pole vault'),
('Sydney', 'McLaughlin', 936576000000, 'USA', '400m Hurdles', '50.68s', 'World record holder in 400m hurdles'),
('Karsten', 'Warholm', 806112000000, 'Norway', '400m Hurdles', '45.94s', 'World record holder in men''s 400m hurdles'),
('Javier', 'Sotomayor', 236390400000, 'Cuba', 'High Jump', '2.45m', 'High jump world record holder since 1993'),

-- More Diversity
('Shaunae', 'Miller-Uibo', 767836800000, 'Bahamas', '400m', '48.37s', 'Olympic champion and world leader in 400m'),
('Wayde', 'van Niekerk', 708739200000, 'South Africa', '400m', '43.03s', 'World record holder in 400m'),
('Peruth', 'Chemutai', 936403200000, 'Uganda', '3000m Steeplechase', '9:01.45', 'Olympic gold medalist in steeplechase'),
('Faith', 'Kipyegon', 762652800000, 'Kenya', '1500m', '3:49.11', 'World champion and Olympic gold medalist'),
('Kelvin', 'Kiptum', 820972800000, 'Kenya', 'Marathon', '2:00:35', 'Fastest marathon time in history');

