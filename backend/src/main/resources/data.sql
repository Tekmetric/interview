INSERT INTO repairjob (
    job_name, user_id, repair_description,
    license_plate, make, model, status, created, last_modified
) VALUES
      ('Job #1','19dfa1e6-6970-45fc-bf62-1a5e50a16888','Brake Pad Replacement','ABC1234','Toyota','Camry','CREATED', '2026-01-01', null),
      ('Job #2','19dfa1e6-6970-45fc-bf62-1a5e50a16888','Tire Inspection','ABC1234','Toyota','Camry','CANCELLED', '2025-12-23', null),
      ('Job #3','3e017209-9817-4e5f-97d5-c32644618fdb','Oil Change & Multipoint inspection','XYZ9876','Honda','Civic','IN_PROGRESS', '2026-01-02', null);