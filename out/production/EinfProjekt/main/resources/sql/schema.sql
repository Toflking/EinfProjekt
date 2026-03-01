CREATE DATABASE IF NOT EXISTS rezeptedb
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE rezeptedb;

CREATE TABLE categories (
    id   INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);
CREATE TABLE areas (
    id   INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);
CREATE TABLE ingredients (
    id   INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);
CREATE TABLE meals (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    external_id INT NULL,
    UNIQUE KEY uq_meals_external_id (external_id),
    name VARCHAR(255) NOT NULL,
    category_id INT NULL,
    area_id INT NULL,
    instructions TEXT,
    thumb VARCHAR(500),
    youtube VARCHAR(500),
    source VARCHAR(500),
    tags VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_meals_category
        FOREIGN KEY (category_id) REFERENCES categories (id),
    CONSTRAINT fk_meals_area
        FOREIGN KEY (area_id) REFERENCES areas (id)
);


CREATE TABLE meal_ingredients (
    meal_id       INT NOT NULL,
    ingredient_id INT NOT NULL,
    measure       VARCHAR(100),
    PRIMARY KEY (meal_id, ingredient_id),
    CONSTRAINT fk_meal_ing_meal
        FOREIGN KEY (meal_id) REFERENCES meals (id)
            ON DELETE CASCADE,
    CONSTRAINT fk_meal_ing_ingredient
        FOREIGN KEY (ingredient_id) REFERENCES ingredients (id)
);