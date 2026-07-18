
-- Employees
INSERT INTO employees (id, name, department, email, role, created_at, updated_at)
VALUES
('0d84bee8-c1c6-46eb-8c21-ba2e05e57dfa', 'Alice Example', 'Engineering', 'alice@example.com', 'MANAGER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('97e12b07-b194-4c96-bf75-c5b77013ec37', 'Bob Example', 'Marketing', 'bob@example.com', 'PRODUCT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Goals
INSERT INTO goals (id, employee_id, name, description, status, due_date, created_at, updated_at)
VALUES
('5e662fa6-073d-4894-844c-430fdfeff49f', '0d84bee8-c1c6-46eb-8c21-ba2e05e57dfa', 'Complete system design', 'Finish system architecture draft', 'NOT_STARTED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('31e5e0cf-6c86-4395-9a24-b77665fe96f3', '0d84bee8-c1c6-46eb-8c21-ba2e05e57dfa', 'Write unit tests', 'Achieve 80% coverage', 'IN_PROGRESS', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('34edf605-e20c-4a50-a135-0b297520ae18', '97e12b07-b194-4c96-bf75-c5b77013ec37', 'Campaign launch', 'Lead Q1 marketing campaign', 'COMPLETED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
