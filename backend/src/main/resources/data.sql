-- Set of blog postings that are initialized on startup
SET @AliceFirstEntry = 'ca7fdab0-8817-4d7f-804f-b8be71495b11';

INSERT INTO Blog_Entry(id, author, version, creation_timestamp, content, deleted)
VALUES(@AliceFirstEntry, 'Alice', 0, '2020-10-10 13:0:05.00+00', 'Sample Blog Content', false);

INSERT INTO Categories(blog_entry_id, blog_entry_author, category, deleted)
VALUES (@AliceFirstEntry, 'Alice', 'NEWS', false),
    (@AliceFirstEntry, 'Alice', 'SPORTS', false),
    (@AliceFirstEntry, 'Alice', 'SCIENCE', false);