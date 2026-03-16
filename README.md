# JavaFX-JDBC-Project
JavaFX-JDBC-Project

# Movie Management System (IMDb Analog)

GUI-додаток на JavaFX для управління базою даних фільмів.
Побудований на архітектурі **MVC** з використанням **JavaFX** та **PostgreSQL**.

---

##  Склад команди
Team Lead (@meshapavlo-glitch)** — Павло Меша - координація, архітектура БД, Pull Requests.
Developer 1 (@zorklin)** — Андрій Федорченков - реалізація логіки JDBC.
Developer 2 (@PopovMaksim)** — Максим Попов - розробка GUI (JavaFX).

---

##  Технологічний стек
- **Java**
- **JavaFX** (Графічний інтерфейс користувача)
- **PostgreSQL** (База даних)
- **JDBC** (Взаємодія з СКБД)
- **Maven** (Збірка та управління залежностями)
- **JUnit 5** (Модульне тестування)

---

## Схема бази даних (ER-діаграма)
erDiagram
    
  MOVIE {
        bigint movie_id PK
        string title
        int release_year
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
          
    ACTOR ||..o{ MOVIE_CAST : "performs"
    MOVIE ||..o{ MOVIE_CAST : "has"
    ROLE ||..o{ MOVIE_CAST : "defines"
    
---

## SQL-запити для створення таблиць бази даних 

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
    Мета: Графічний інтерфейс для керування таблицею MOVIE (фільми).
    Основні функції:
       - Перегляд: Відображення списку фільмів.
       - CRUD-операції: Додавання, редагування та видалення записів про фільми.
       - Пошук та фільтрація: Швидкий пошук фільмів за назвою, роком випуску або актором.
       - Перегляд акторского складу: Відображення акторів обраного фільму.
      
--- 
  ##  Правила злиття гілок (Merge & Pull Request Policy)
    Процес додавання коду розробником:
       - Розробник створює гілку від main.
       - Після завершення роботи над завданням робить push своєї гілки на GitHub.
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
     5.Реалізація функцій CRUD та пошуку.

---    

  ##  Як запустити
   1. Переконайтеся, що PostgreSQL запущений.
   2. Налаштуйте доступ у файлі `src/main/resources/db.properties`.
   3. Запустіть клас `org.example.AppLauncher`.

## 📚 Документація
Технічна документація згенерована за допомогою JavaDoc і знаходиться в папці `target/site/apidocs/index.html`.
