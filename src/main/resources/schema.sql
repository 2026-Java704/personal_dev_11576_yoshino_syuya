DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS expenses;
DROP TABLE IF EXISTS users;

CREATE TABLE categories (
    id SERIAL PRIMARY KEY,
    name VARCHAR(20) NOT NULL
);

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(20) NOT NULL,
    income INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE expenses (
    id SERIAL PRIMARY KEY,
    category_id INTEGER NOT NULL,
    date DATE NOT NULL,
    price INTEGER NOT NULL,
    name VARCHAR(100) NOT NULL,
    FOREIGN KEY (category_id) REFERENCES categories(id)
);
