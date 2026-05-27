package org.example.service;

import org.example.model.Movie;
import org.example.service.DatabaseService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseServiceTest {

    private DatabaseService dbService;
    private Connection testConnection;

    @BeforeEach
    void setUp() throws SQLException {
        dbService = new DatabaseService();
        testConnection = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", "sa", "");
        
        try (Statement stmt = testConnection.createStatement()) {
            // Очищаємо базу перед кожним тестом, щоб уникнути помилки "Table already exists"
            stmt.execute("DROP TABLE IF EXISTS movies");
            
            // Створюємо таблицю наново
            stmt.execute("CREATE TABLE movies (id BIGINT PRIMARY KEY, title VARCHAR(255), releaseYear INT)");
            stmt.execute("INSERT INTO movies (id, title, releaseYear) VALUES (1, 'The Matrix', 1999)");
        }
    }

    @AfterEach
    void tearDown() throws SQLException {
        testConnection.close();
    }

    @Test
    void testGetAllMovies_Success() {
        // Уявимо, що dbService.getAllMovies() звертається до нашої H2 БД
        assertDoesNotThrow(() -> {
            List<Movie> movies = dbService.getAllMovies();
            // Перевірка, що метод не падає і щось повертає (залежить від реалізації)
            assertNotNull(movies); 
        });
    }

    @Test
    void testDatabaseConnection_ExceptionScenario() throws Exception {
        DatabaseService faultyService = new DatabaseService();

        // 1. Отримуємо доступ до приватного поля props через рефлексію
        java.lang.reflect.Field propsField = DatabaseService.class.getDeclaredField("props");
        propsField.setAccessible(true);
        java.util.Properties props = (java.util.Properties) propsField.get(faultyService);
        
        // 2. Змінюємо URL на свідомо невалідний, щоб підключення гарантовано впало
        props.setProperty("db.url", "jdbc:invalid_db_driver://localhost/wrongdb"); 
        
        // 3. Тепер виклик має точно викинути SQLException
        assertThrows(SQLException.class, () -> {
            faultyService.getAllMovies(); 
        }, "Очікується SQLException при неможливості підключитися до БД через невірний URL");
    }
}