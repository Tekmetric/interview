-- Set of blog postings that are initialized on startup
SET @AliceFirstEntry = 'ca7fdab0-8817-4d7f-804f-b8be71495b11';

INSERT INTO Blog_Entry(id, author, version, creation_timestamp, content, deleted)
VALUES (@AliceFirstEntry, 'Alice', 0, current_timestamp(), 'Sample Blog Content', false),
    (random_uuid(), 'Bob', 0, current_timestamp(), 'I wrote a blog!', false),
    (random_uuid(), 'Alice', 0, current_timestamp(), 'Random blog 1', false),
    (random_uuid(), 'Alice', 0, current_timestamp(), 'Random blog 2', false),
    (random_uuid(), 'Alice', 0, current_timestamp(), 'Random blog 3', false),
    (random_uuid(), 'Alice', 0, current_timestamp(), 'Random blog 4', false),
    (random_uuid(), 'Alice', 0, current_timestamp(), 'Random blog 5', false),
    (random_uuid(), 'Alice', 0, current_timestamp(), 'Random blog 6', false),
    (random_uuid(), 'Alice', 0, current_timestamp(), 'Random blog 7', false),
    (random_uuid(), 'Alice', 0, current_timestamp(), 'Random blog 8', false),
    (random_uuid(), 'Alice', 0, current_timestamp(), 'Random blog 9', false),
    (random_uuid(), 'Alice', 0, current_timestamp(), 'Random blog 10', false),
    (random_uuid(), 'Alice', 0, current_timestamp(), 'Random blog 11', false),
    (random_uuid(), 'Alice', 0, current_timestamp(), 'Random blog 12', false),
    (random_uuid(), 'Alice', 0, current_timestamp(), 'Random blog 13', false),
    (random_uuid(), 'Alice', 0, current_timestamp(), 'Random blog 14', false),
    (random_uuid(), 'Alice', 0, current_timestamp(), 'Random blog 15', false),
    (random_uuid(), 'Alice', 0, current_timestamp(), 'Random blog 16', false),
    (random_uuid(), 'Alice', 0, current_timestamp(), 'Random blog 17', false),
    (random_uuid(), 'Alice', 0, current_timestamp(), 'Random blog 18', false),
    (random_uuid(), 'Alice', 0, current_timestamp(), 'Random blog 19', false),
    (random_uuid(), 'Alice', 0, current_timestamp(), 'Random blog 20', false),
    (random_uuid(), 'Alice', 0, current_timestamp(), 'Random blog 21', false),
    (random_uuid(), 'Alice', 0, current_timestamp(), 'Random blog 22', false);

INSERT INTO Categories(blog_entry_id, blog_entry_author, category, deleted)
VALUES (@AliceFirstEntry, 'Alice', 'ART', false),
    (@AliceFirstEntry, 'Alice', 'SPORTS', false),
    (@AliceFirstEntry, 'Alice', 'ENTERTAINMENT', false);

INSERT INTO Categories(blog_entry_id, blog_entry_author, category, deleted)
SELECT temp.id, temp.author, 'SCIENCE', false FROM Blog_Entry temp
WHERE temp.author = 'Alice' AND temp.id != @AliceFirstEntry;

INSERT INTO Categories(blog_entry_id, blog_entry_author, category, deleted)
SELECT temp.id, temp.author, 'SPORTS', false FROM Blog_Entry temp
WHERE temp.author = 'Alice' AND temp.id != @AliceFirstEntry;
