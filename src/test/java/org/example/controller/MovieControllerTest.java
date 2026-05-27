package org.example.controller;

import javafx.application.Platform;
import javafx.scene.control.TableView;
import org.example.model.Movie;
import org.example.service.DatabaseService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MovieControllerTest {

    private MovieController controller;
    private DatabaseService mockDbService;

    @BeforeAll
    static void initJFX() {
        // Ініціалізація JavaFX Toolkit для тестування UI-компонентів без вікна
        Platform.startup(() -> {});
    }

    @BeforeEach
    void setUp() throws Exception {
        controller = new MovieController();
        mockDbService = mock(DatabaseService.class);

        // Підміна залежності DatabaseService через рефлексію
        Field dbServiceField = MovieController.class.getDeclaredField("dbService");
        dbServiceField.setAccessible(true);
        dbServiceField.set(controller, mockDbService);
        
        // Підміна TableView для уникнення NullPointerException
        Field tableField = MovieController.class.getDeclaredField("movieTable");
        tableField.setAccessible(true);
        tableField.set(controller, new TableView<Movie>());
    }

    @Test
    void testRefreshTable_Success() throws Exception {
        // Налаштування моку
        Movie m1 = new Movie(); 
        Movie m2 = new Movie();
        when(mockDbService.getAllMovies()).thenReturn(Arrays.asList(m1, m2));

        // Виклик логіки
        Platform.runLater(() -> {
            try {
                // Викликаємо private метод refreshTable через рефлексію
                java.lang.reflect.Method refreshMethod = MovieController.class.getDeclaredMethod("refreshTable");
                refreshMethod.setAccessible(true);
                refreshMethod.invoke(controller);
            } catch (Exception e) {
                fail("Метод refreshTable не повинен кидати виключень");
            }
        });
        
        // Перевіряємо, що сервіс був викликаний рівно 1 раз [3]
        verify(mockDbService, times(1)).getAllMovies();
    }

    @Test
    void testUpdateMovieInDb_SQLExceptionHandling() throws Exception {
        Movie movieToUpdate = new Movie();
        
        // Використовуємо ArgumentMatchers.any() для надійності, щоб мок гарантовано 
        // реагував на будь-який переданий об'єкт Movie
        doThrow(new SQLException("DB Connection Error"))
            .when(mockDbService).updateMovie(org.mockito.ArgumentMatchers.any(Movie.class));

        java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);
        
        // Контейнер для збереження помилок, які можуть тихо статися всередині Platform.runLater
        java.util.concurrent.atomic.AtomicReference<Throwable> errorRef = new java.util.concurrent.atomic.AtomicReference<>();

        Platform.runLater(() -> {
            // ВАЖЛИВО: Mockito.mockConstruction діє лише в межах поточного потоку.
            // Тому ми поміщаємо його сюди, де й фактично буде створюватися Alert!
            try (org.mockito.MockedConstruction<javafx.scene.control.Alert> mockedAlert = 
                 org.mockito.Mockito.mockConstruction(javafx.scene.control.Alert.class)) {
                
                java.lang.reflect.Method updateMethod = MovieController.class.getDeclaredMethod("updateMovieInDb", Movie.class);
                updateMethod.setAccessible(true);
                
                // Цей виклик має викинути SQLException, спіймати його, викликати showError, 
                // де Alert заміниться моком (нічого не зробить), і потім запустити refreshTable()
                updateMethod.invoke(controller, movieToUpdate);
                
            } catch (Throwable t) {
                // Якщо трапляється будь-яка непередбачувана помилка (наприклад з UI), ми її фіксуємо
                errorRef.set(t);
            } finally {
                latch.countDown();
            }
        });

        // Чекаємо максимум 3 секунди на виконання потоку JavaFX
        latch.await(3, java.util.concurrent.TimeUnit.SECONDS);
        
        // Якщо у JavaFX потоці сталася прихована помилка, тест впаде з її детальним описом
        if (errorRef.get() != null) {
            fail("Прихована помилка у потоці JavaFX: " + errorRef.get());
        }

        // Тепер метод refreshTable відпрацює повністю, і getAllMovies() буде викликано
        verify(mockDbService, times(1)).getAllMovies();
    }
}
