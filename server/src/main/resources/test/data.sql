-- Удаление данных из таблиц перед добавлением новых данных
DELETE FROM comments;
DELETE FROM bookings;
DELETE FROM items;
DELETE FROM request;
DELETE FROM users;
--
---- Обнуление счетчиков идентификаторов (ID)
ALTER table users ALTER COLUMN id RESTART WITH 1;
ALTER table comments ALTER COLUMN id RESTART WITH 1;
ALTER table bookings ALTER COLUMN id RESTART WITH 1;
ALTER table items ALTER COLUMN id RESTART WITH 1;
ALTER table request ALTER COLUMN id RESTART WITH 1;



--ALTER SEQUENCE users_id_seq RESTART WITH 1;
--ALTER SEQUENCE request_id_seq RESTART WITH 1;
--ALTER SEQUENCE items_id_seq RESTART WITH 1;
--ALTER SEQUENCE bookings_id_seq RESTART WITH 1;
--ALTER SEQUENCE comments_id_seq RESTART WITH 1;

-- Добавление тестовых пользователей
INSERT INTO users (name, email)
VALUES
    ('User 1', 'user1@example.com'),
    ('User 2', 'user2@example.com');

-- Добавление тестовых заявок
INSERT INTO request (description, created, requester_id)
VALUES
    ('Request 1', CURRENT_TIMESTAMP, 1),
    ('Request 2', CURRENT_TIMESTAMP, 2);

-- Добавление тестовых предметов
INSERT INTO items (name, description, is_available, owner_id, request_id)
VALUES
    ('Item 1', 'Item 1 description abc', true, 1, 1),
    ('Item 2', 'Item 2 description', true, 2, 2);

-- Добавление тестовых бронирований
INSERT INTO bookings (start_date, end_date, item_id, booker_id, status)
VALUES
    ('2023-08-20 10:00:00', '2023-08-20 12:00:00', 1, 2, 'APPROVED'),
    ('2023-08-21 14:00:00', '2023-08-21 16:00:00', 1, 2, 'REJECTED'),
    ('2023-08-22 09:00:00', '2023-08-22 11:00:00', 2, 1, 'APPROVED'),
    ('2023-08-23 13:00:00', '2023-08-23 15:00:00', 2, 1, 'REJECTED');

-- Добавление тестовых комментариев
INSERT INTO comments (text, created, item_id, author_id)
VALUES
    ('Comment 1', CURRENT_TIMESTAMP, 1, 1),
    ('Comment 2', CURRENT_TIMESTAMP, 1, 2),
    ('Comment 3', CURRENT_TIMESTAMP, 2, 1);