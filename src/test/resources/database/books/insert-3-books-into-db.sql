INSERT INTO categories (id, name, description, is_deleted)
values (1, 'Action', 'Movie action', false);

INSERT INTO categories (id, name, description, is_deleted)
values (2, 'Adventure', 'Movie adventure', false);

INSERT INTO books (id, title, author, isbn, price, description, cover_image, is_deleted)
VALUES (1, 'The Bourne Identity', 'Robert Ludlum', '978-0553270433', 15.99, 'History about memory loss', 'https://example.com/images/bourne_cover.jpg', false);

INSERT INTO books (id, title, author, isbn, price, description, cover_image, is_deleted)
VALUES (2, 'First Blood', 'David Morrell', '978-0446364409', 14.50, 'History about Rembo', 'https://example.com/images/rambo_cover.jpg', false);

INSERT INTO books (id, title, author, isbn, price, description, cover_image, is_deleted)
VALUES (3, 'Treasure Island', 'Robert Louis Stevenson', '978-0140437683', 12.99, 'About pirates', 'https://example.com/images/treasure_island_cover.jpg', false);

INSERT INTO books_categories (book_id, category_id) VALUES (1, 1);
INSERT INTO books_categories (book_id, category_id) VALUES (2, 1);
INSERT INTO books_categories (book_id, category_id) VALUES (3, 2);
