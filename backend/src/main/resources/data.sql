INSERT INTO repairjob (
    name, user_id, repair_description,
    license_plate, make, model, status,
    created, last_modified
) VALUES
      ('Brake Pad Replacement','19dfa1e6-6970-45fc-bf62-1a5e50a16888',
       'replace brake pads','ABC1234','Toyota','Camry','CREATED',
       '2026-01-01 09:00:00','2026-01-01 09:00:00'),
      ('Tire Inspection','19dfa1e6-6970-45fc-bf62-1a5e50a16888',
       'do a tire inspection','DEF1234','Chevy','Camaro','CREATED',
       '2025-12-23 14:30:00','2025-12-23 14:30:00'),
      ('Battery Replacement','19dfa1e6-6970-45fc-bf62-1a5e50a16888',
       'replace the battery for the customer','DEF1234','Chevy','Camaro','IN_PROGRESS',
       '2025-12-23 14:30:00','2025-12-23 14:30:00'),
      ('Oil Change & Multipoint inspection','3e017209-9817-4e5f-97d5-c32644618fdb',
       'do an oil change and check all the core components','XYZ9876','Honda','Civic','IN_PROGRESS',
       '2026-01-02 11:15:00','2026-01-02 11:15:00');
