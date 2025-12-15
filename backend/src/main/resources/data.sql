-- Set of blog postings that are initialized on startup
SET @AliceFirstEntry = 'ca7fdab0-8817-4d7f-804f-b8be71495b11';

INSERT INTO Blog_Entry(id, author, version, creation_timestamp, last_update_timestamp, content, title, deleted)
VALUES (@AliceFirstEntry, 'Alice', 0, current_timestamp(), current_timestamp(), 'Sample Blog Content', 'My first blog', false),
    (random_uuid(), 'Bob', 0, current_timestamp(), current_timestamp(), 'I wrote a blog!', 'Another post', false),
    (random_uuid(), 'Alice', 0, current_timestamp(), current_timestamp(), 'Random blog 1', 'Update 1', false),
    (random_uuid(), 'Alice', 0, current_timestamp(), current_timestamp(), 'Random blog 2', 'Update 2', false),
    (random_uuid(), 'Alice', 0, current_timestamp(), current_timestamp(), 'Random blog 3', 'Update 3', false),
    (random_uuid(), 'Alice', 0, current_timestamp(), current_timestamp(), 'Random blog 4', 'Update 4', false),
    (random_uuid(), 'Alice', 0, current_timestamp(), current_timestamp(), 'Random blog 5', 'Update 5', false),
    (random_uuid(), 'Alice', 0, current_timestamp(), current_timestamp(), 'Random blog 6', 'Update 6', false),
    (random_uuid(), 'Alice', 0, current_timestamp(), current_timestamp(), 'Random blog 7', 'Update 7', false),
    (random_uuid(), 'Alice', 0, current_timestamp(), current_timestamp(), 'Random blog 8', 'Update 8', false),
    (random_uuid(), 'Alice', 0, current_timestamp(), current_timestamp(), 'Random blog 9', 'Update 9', false),
    (random_uuid(), 'Alice', 0, current_timestamp(), current_timestamp(), 'Random blog 10', 'Update 10', false),
    (random_uuid(), 'Alice', 0, current_timestamp(), current_timestamp(), 'Random blog 11', 'Update 11', false),
    (random_uuid(), 'Alice', 0, current_timestamp(), current_timestamp(), 'Random blog 12', 'Update 12', false),
    (random_uuid(), 'Alice', 0, current_timestamp(), current_timestamp(), 'Random blog 13', 'Update 13', false),
    (random_uuid(), 'Alice', 0, current_timestamp(), current_timestamp(), 'Random blog 14', 'Update 14', false),
    (random_uuid(), 'Alice', 0, current_timestamp(), current_timestamp(), 'Random blog 15', 'Update 15', false),
    (random_uuid(), 'Alice', 0, current_timestamp(), current_timestamp(), 'Random blog 16', 'Update 16', false),
    (random_uuid(), 'Alice', 0, current_timestamp(), current_timestamp(), 'Random blog 17', 'Update 17', false),
    (random_uuid(), 'Alice', 0, current_timestamp(), current_timestamp(), 'Random blog 18', 'Update 18', false),
    (random_uuid(), 'Alice', 0, current_timestamp(), current_timestamp(), 'Random blog 19', 'Update 19', false),
    (random_uuid(), 'Alice', 0, current_timestamp(), current_timestamp(), 'Random blog 20', 'Update 20', false),
    (random_uuid(), 'Alice', 0, current_timestamp(), current_timestamp(), 'Random blog 21', 'Update 21', false),
    (random_uuid(), 'Alice', 0, current_timestamp(), current_timestamp(), 'Random blog 22', 'Update 22', false);

INSERT INTO Categories(blog_entry_id, blog_entry_author, category)
VALUES (@AliceFirstEntry, 'Alice', 'ART'),
    (@AliceFirstEntry, 'Alice', 'ENTERTAINMENT');

INSERT INTO Categories(blog_entry_id, blog_entry_author, category)
SELECT temp.id, temp.author, 'SCIENCE' FROM Blog_Entry temp
WHERE temp.author = 'Alice';

INSERT INTO Categories(blog_entry_id, blog_entry_author, category)
SELECT temp.id, temp.author, 'SPORTS' FROM Blog_Entry temp
WHERE temp.author = 'Alice';
