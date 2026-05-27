package org.example.controller;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.example.model.Actor;
import org.example.model.Movie;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ActorsControllerTest {

    private ActorsController controller;
    private Label testLabel;
    private TableView<Actor> testTable;

    @BeforeAll
    static void initJFX() {
        Platform.startup(() -> {});
    }

    @BeforeEach
    void setUp() throws Exception {
        controller = new ActorsController();
        testLabel = new Label();
        testTable = new TableView<>();

        // Інжектимо UI-елементи
        Field labelField = ActorsController.class.getDeclaredField("movieTitleLabel");
        labelField.setAccessible(true);
        labelField.set(controller, testLabel);

        Field tableField = ActorsController.class.getDeclaredField("actorsTable");
        tableField.setAccessible(true);
        tableField.set(controller, testTable);
        
        Field nameColField = ActorsController.class.getDeclaredField("nameColumn");
        nameColField.setAccessible(true);
        nameColField.set(controller, new TableColumn<Actor, String>());
        
        Field roleColField = ActorsController.class.getDeclaredField("roleColumn");
        roleColField.setAccessible(true);
        roleColField.set(controller, new TableColumn<Actor, String>());
    }

    @Test
    void testInitData_FormatsLabelAndPopulatesTable() {
        // Підготовка даних
        Movie movie = new Movie();
        movie.setTitle("The Matrix");

        List<Actor> actors = new ArrayList<>();
        Actor actor1 = new Actor(); // припускаємо, що створюється порожнім [5]
        actors.add(actor1);

        // Виклик методу
        Platform.runLater(() -> {
            controller.initData(movie, actors);

            // Перевірка логіки [4]
            assertEquals("Актори фільму: The Matrix", testLabel.getText(), "Лейбл має містити назву фільму");
            assertEquals(1, testTable.getItems().size(), "Таблиця має містити одного актора");
        });
    }
}