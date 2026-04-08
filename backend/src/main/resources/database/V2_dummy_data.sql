MERGE INTO books (id, title, author, isbn, genre, price, published_at, description, version)
KEY (isbn)
VALUES
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11',
     'Clean Code', 'Robert C. Martin', '978-0132350884',
     'TECHNOLOGY', 35.99, '2008-08-01',
     'A handbook of agile software craftsmanship.', 0),

    ('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a22',
     'Designing Data-Intensive Applications', 'Martin Kleppmann', '978-1449373320',
     'TECHNOLOGY', 49.99, '2017-04-18',
     'The big ideas behind reliable, scalable and maintainable systems.', 0),

    ('c2eebc99-9c0b-4ef8-bb6d-6bb9bd380a33',
     'The Pragmatic Programmer', 'David Thomas', '978-0135957059',
     'TECHNOLOGY', 42.99, '2019-09-13',
     'Your journey to mastery, 20th Anniversary Edition.', 0),

    ('d3eebc99-9c0b-4ef8-bb6d-6bb9bd380a44',
     'Domain-Driven Design', 'Eric Evans', '978-0321125217',
     'TECHNOLOGY', 54.99, '2003-08-22',
     'Tackling complexity in the heart of software.', 0),

    ('e4eebc99-9c0b-4ef8-bb6d-6bb9bd380a55',
     'Refactoring', 'Martin Fowler', '978-0134757599',
     'TECHNOLOGY', 44.99, '2018-11-20',
     'Improving the design of existing code, 2nd Edition.', 0);