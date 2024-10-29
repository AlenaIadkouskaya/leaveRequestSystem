--CREATE TABLE authorities (
--    role_id BIGSERIAL PRIMARY KEY,
--    role_name VARCHAR(50) NOT NULL
--);
--
---- Уникальный индекс для роли
--CREATE UNIQUE INDEX idx_role_name ON authorities(role_name);
--
---- Вставляем роли
--INSERT INTO authorities (role_name) VALUES
--('ROLE_USER'),
--('ROLE_HR'),
--('ROLE_MANAGER'),
--('ROLE_ACCOUNTANT');
--
---- Создаем таблицу users
--CREATE TABLE users (
--    user_id BIGSERIAL PRIMARY KEY,
--    username VARCHAR(50) NOT NULL,
--    password_hash VARCHAR(255) NOT NULL,
--    last_name VARCHAR(50) NOT NULL,
--    first_name VARCHAR(50) NOT NULL,
--    email VARCHAR(100) NOT NULL UNIQUE,
--    role_id BIGINT,
--    FOREIGN KEY (role_id) REFERENCES authorities(role_id) ON DELETE SET NULL
--);
--
---- Индекс для email
--CREATE INDEX idx_email ON users(email);