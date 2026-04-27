package org.example.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.model.Actor;
import org.example.model.Movie;

import java.util.List;

/**
 * Контролер для вікна перегляду акторів.
 */
public class ActorsController {

    @FXML private TableView<Actor> actorsTable;
    @FXML private TableColumn<Actor, String> nameColumn;
    @FXML private TableColumn<Actor, String> roleColumn;
    @FXML private Label movieTitleLabel;

    /**
     * Ініціалізує дані у вікні акторів.
     * @param movie об'єкт фільму, для якого виводяться актори
     * @param actors список акторів із бази даних
     */
    public void initData(Movie movie, List<Actor> actors) {
        // Встановлюємо заголовок з назвою обраного фільму
        movieTitleLabel.setText("Актори фільму: " + movie.getTitle());

        // Налаштовуємо відповідність колонок полям класу Actor
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));

        // Завантажуємо список у таблицю
        actorsTable.setItems(FXCollections.observableArrayList(actors));
    }
}  
