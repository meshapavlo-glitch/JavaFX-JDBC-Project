# JavaFX-JDBC-Project
JavaFX-JDBC-Project

# Movie Management System (IMDb Analog)

GUI-додаток на JavaFX для управління базою даних фільмів, акторів та їхніх ролей.

---

##  Склад команди
Team Lead (@meshapavlo-glitch)** — Павло Меша - координація, архітектура БД, Pull Requests.
Developer 1 (@zorklin)** — Андрій Федорченков - реалізація логіки JDBC.
Developer 2 (@PopovMaksim)** — Максим Попов - розробка GUI (JavaFX).

---

##  Технологічний стек
JavaFX (Інтерфейс користувача)
Maven (Управління залежностями)
JDBC  (Зв'язок з СУБД)
PostgreSQL (База даних)

---
## Схема бази даних (ER-діаграма)
erDiagram
    
    COUNTRY {
        bigint country_id PK
        string name UK
    }

    MOVIE {
        bigint movie_id PK
        string title
        int release_year
    }

    MOVIE_COUNTRY {
        bigint movie_id PK, FK
        bigint country_id PK, FK
    }

    ACTOR {
        bigint actor_id PK
        string full_name
        date birth_date
    }

    ROLE {
        bigint role_id PK
        string role_title
    }

    MOVIE_CAST {
        bigint mc_id PK
        bigint movie_id FK
        bigint actor_id FK
        bigint role_id FK
    }      

    COUNTRY ||--o{ MOVIE_COUNTRY : "involved in"
    MOVIE ||--o{ MOVIE_COUNTRY : "filmed in"
    
    ACTOR ||..o{ MOVIE_CAST : "performs"
    MOVIE ||..o{ MOVIE_CAST : "has"
    ROLE ||..o{ MOVIE_CAST : "defines"
---
## SQL-запити для створення таблиць бази даних 
-- Таблиця країн
CREATE TABLE COUNTRY (
    country_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

-- Таблиця фільмів
CREATE TABLE MOVIE (
    movie_id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    release_year INTEGER
);

-- Таблиця акторів
CREATE TABLE ACTOR (
    actor_id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    birth_date DATE
);

-- Таблиця ролей
CREATE TABLE ROLE (
    role_id BIGSERIAL PRIMARY KEY,
    role_title VARCHAR(255) NOT NULL
);

-- Таблиця для зв'язку фільмів та країн 
CREATE TABLE MOVIE_COUNTRY (
    movie_id BIGINT REFERENCES MOVIE(movie_id) ON DELETE CASCADE,
    country_id BIGINT REFERENCES COUNTRY(country_id) ON DELETE CASCADE,
    PRIMARY KEY (movie_id, country_id)
);

-- Таблиця акторського складу (Зв'язок Movie + Actor + Role)
CREATE TABLE MOVIE_CAST (
    mc_id BIGSERIAL PRIMARY KEY,
    movie_id BIGINT REFERENCES MOVIE(movie_id) ON DELETE CASCADE,
    actor_id BIGINT REFERENCES ACTOR(actor_id) ON DELETE CASCADE,
    role_id BIGINT REFERENCES ROLE(role_id) ON DELETE SET NULL
);

---
  ##  Опис застосунку 
    Назва: Movie Manager Pro (IMDb Analog)
    Мета: Графічний інтерфейс для керування реляційною базою даних кіноіндустрії.
    Основні функції:
       - Перегляд: Відображення списку фільмів, акторів та їхніх ролей у зручних таблицях.
       - CRUD-операції: Додавання, редагування та видалення записів про фільми, країни та акторський склад.
       - Пошук та фільтрація: Швидкий пошук фільмів за назвою, роком випуску або актором.
       - Зв'язки: Підтримка складної структури "багато-до-багатьох" (один фільм — багато країн, один актор — багато ролей).
--- 
  ##  Правила злиття гілок (Merge & Pull Request Policy)
    Процес додавання коду розробником:
       - Розробник створює гілку від main.
       - Після завершення роботи над завданням він робить push своєї гілки на GitHub.
       - Створює Pull Request (PR) до гілки main.
       - Team Lead переглядає код (Code Review):
           -- Якщо є помилки — пише коментарі для виправлення.
           -- Якщо все добре — підтверджує (Approve) і робить Merge.
       - Після злиття гілка фічі видаляється.
--- 
  ##  План робіт (Roadmap)
     1.Створення БД та написання SQL-скриптів.
     2.Розробка JDBC-шару
     3.Створення візуального інтерфейсу в Scene Builder.
     4.Об'єднання логіки з GUI через контролери.
     5.Реалізація функцій пошуку.
