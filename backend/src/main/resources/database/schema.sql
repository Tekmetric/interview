CREATE SEQUENCE IF NOT EXISTS employee_seq START WITH 1;
CREATE SEQUENCE IF NOT EXISTS vet_seq START WITH 1;
CREATE SEQUENCE IF NOT EXISTS animal_seq START WITH 1;

CREATE TABLE employees (
    id BIGINT DEFAULT NEXT VALUE FOR employee_seq PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    job_title VARCHAR(255) NOT NULL,
    contact_information VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Index for email lookups in employees
CREATE INDEX idx_employee_contact ON employees(contact_information);

CREATE TABLE vets (
    id BIGINT DEFAULT NEXT VALUE FOR vet_seq PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    specialization VARCHAR(255) NOT NULL,
    contact_information VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Index for email lookups in vets
CREATE INDEX idx_vet_contact ON vets(contact_information);

CREATE TABLE animals (
    id BIGINT DEFAULT NEXT VALUE FOR animal_seq PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    species VARCHAR(255) NOT NULL,
    breed VARCHAR(255) NOT NULL,
    date_of_birth DATE NOT NULL,
    employee_id BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES employees(id)
);

-- Index for name search
CREATE INDEX idx_animal_name ON animals(name);

-- Index for date of birth range queries
CREATE INDEX idx_animal_dob ON animals(date_of_birth);

-- Index for employee-animal relationship
CREATE INDEX idx_animal_employee ON animals(employee_id);

CREATE TABLE animal_vet (
    animal_id BIGINT NOT NULL,
    vet_id BIGINT NOT NULL,
    PRIMARY KEY (animal_id, vet_id),
    FOREIGN KEY (animal_id) REFERENCES animals(id),
    FOREIGN KEY (vet_id) REFERENCES vets(id)
);

-- Index for animal-vet relationship when searching all vets for an animal
CREATE INDEX idx_animal_vet_animal ON animal_vet(animal_id);