-- Insert sample employees
INSERT INTO employees (name, job_title, contact_information) VALUES
    ('John Doe', 'Animal Caretaker', 'john.doe@example.com'),
    ('Jane Smith', 'Senior Caretaker', 'jane.smith@example.com');

-- Insert sample vets
INSERT INTO vets (name, specialization, contact_information) VALUES
    ('Dr. Sarah Wilson', 'General Practice', 'sarah.wilson@example.com'),
    ('Dr. Michael Brown', 'Surgery', 'michael.brown@example.com');

-- Insert sample animals
INSERT INTO animals (name, species, breed, date_of_birth, employee_id) VALUES
    ('Max', 'Dog', 'Golden Retriever', '2022-01-15', 1),
    ('Luna', 'Cat', 'Siamese', '2021-06-20', 2),
    ('Rocky', 'Dog', 'German Shepherd', '2023-03-10', 1);

-- Link animals with vets
INSERT INTO animal_vet (animal_id, vet_id) VALUES
    (1, 1),  -- Max is treated by Dr. Wilson
    (1, 2),  -- Max is also treated by Dr. Brown
    (2, 1),  -- Luna is treated by Dr. Wilson
    (3, 2);  -- Rocky is treated by Dr. Brown