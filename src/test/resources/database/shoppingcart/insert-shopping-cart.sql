INSERT INTO users (id, email, password, first_name, last_name, shipping_address, is_deleted)
VALUES (2, 'den@gmail.com', '$2a$10$sLohZN7TVPfNM04hdmX3keySYPCbC2Vn4VQHegMBHqTd1M5gpJyUa', 'Denis', 'Basarab', 'Kievska 12', false);

INSERT INTO users_roles (user_id, role_id)
VALUES (2, 1);

INSERT INTO shopping_carts (id, is_deleted)
VALUES (2, false);

INSERT INTO cart_items (id, shopping_cart_id, book_id, quantity)
VALUES (1, 2, 1, 4);
