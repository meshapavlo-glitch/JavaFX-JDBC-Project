package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.model.Movie;
import org.example.service.DatabaseService;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;
import javafx.scene.control.Alert;

import org.example.model.Actor;

/**
 * Контролер для керування інтерфейсом фільмів.
 */
public class MovieController {

    // Ці змінні мають збігатися з fx:id у вашому fxml файлі
    @FXML private TableView<Movie> movieTable;
    @FXML private TableColumn<Movie, Long> idColumn;
    @FXML private TableColumn<Movie, String> titleColumn;
    @FXML private TableColumn<Movie, Integer> yearColumn;

    @FXML private TextField searchTitleField;
    @FXML private TextField searchYearField;

    private final DatabaseService dbService = new DatabaseService();

    /**
     * Метод initialize викликається автоматично після завантаження FXML.
     */
    @FXML
    public void initialize() {
        movieTable.setEditable(true); // Дозволяємо редагування таблиці

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        // Налаштування колонки Title для редагування
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleColumn.setCellFactory(javafx.scene.control.cell.TextFieldTableCell.forTableColumn());
        titleColumn.setOnEditCommit(event -> {
            Movie movie = event.getRowValue();
            movie.setTitle(event.getNewValue());
            updateMovieInDb(movie);
        });

        // Налаштування колонки Year для редагування
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("releaseYear"));
        // Для чисел використовуємо спеціальний конвертер
        yearColumn.setCellFactory(javafx.scene.control.cell.TextFieldTableCell.forTableColumn(new javafx.util.converter.IntegerStringConverter()));
        yearColumn.setOnEditCommit(event -> {
            Movie movie = event.getRowValue();
            movie.setReleaseYear(event.getNewValue());
            updateMovieInDb(movie);
        });
        // Завантажуємо дані при старті
        refreshTable();
    }

    @FXML
    private void refreshTable() {
        try {
            List<Movie> movies = dbService.getAllMovies();
            ObservableList<Movie> observableList = FXCollections.observableArrayList(movies);
            movieTable.setItems(observableList);
        } catch (SQLException e) {
            System.err.println("Помилка оновлення таблиці: " + e.getMessage());
        }
    }

    @FXML
    private void handleSearch() {
        String titleQuery = searchTitleField.getText().trim();
        String yearQuery = searchYearField.getText().trim();

        try {
            // Викликаємо новий універсальний метод
            List<Movie> results = dbService.searchMovies(titleQuery, yearQuery);

            movieTable.setItems(FXCollections.observableArrayList(results));

            if (results.isEmpty()) {
                System.out.println("Нічого не знайдено.");
            }
        } catch (NumberFormatException e) {
            showError("Рік має бути числом!");
        } catch (SQLException e) {
            showError("Помилка бази даних: " + e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        // 1. Отримуємо вибраний рядок
        Movie selectedMovie = movieTable.getSelectionModel().getSelectedItem();

        if (selectedMovie == null) {
            showError("Будь ласка, виберіть фільм у таблиці для видалення!");
            return;
        }

        // 2. Викликаємо сервіс для видалення з БД
        try {
            dbService.deleteMovie(selectedMovie.getId());

            // 3. Оновлюємо таблицю, щоб видалений фільм зник
            refreshTable();
            System.out.println("Фільм успішно видалено: " + selectedMovie.getTitle());

        } catch (SQLException e) {
            showError("Не вдалося видалити фільм: " + e.getMessage());
        }
    }

    @FXML
    private void handleAdd() {
        String title = searchTitleField.getText().trim();
        String yearStr = searchYearField.getText().trim();

        // Валідація на порожні поля
        if (title.isEmpty() || yearStr.isEmpty()) {
            showError("Заповніть назву та рік для додавання!");
            return;
        }

        try {
            // Конвертація року
            int year = Integer.parseInt(yearStr);

            // Запит до БД через сервіс
            dbService.addMovie(title, year);

            // Очищення полів та оновлення таблиці
            searchTitleField.clear();
            searchYearField.clear();
            refreshTable();

            System.out.println("✅ Фільм додано успішно!");

        } catch (NumberFormatException e) {
            showError("Рік має бути цілим числом!");
        } catch (SQLException e) {
            showError("Помилка бази даних: " + e.getMessage());
        }
    }

    private void updateMovieInDb(Movie movie) {
        try {
            dbService.updateMovie(movie);
            System.out.println("✅ Оновлено: " + movie.getTitle());
        } catch (SQLException e) {
            showError("Помилка оновлення: " + e.getMessage());
            refreshTable(); // Повертаємо старі дані, якщо сталася помилка
        }
    }

    @FXML
    private void handleUpdate() {
        Movie selected = movieTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                selected.setTitle(searchTitleField.getText());
                selected.setReleaseYear(Integer.parseInt(searchYearField.getText()));
                dbService.updateMovie(selected);
                refreshTable();
            } catch (Exception e) {
                showError("Помилка оновлення: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleShowActors() {
        // Отримуємо вибраний фільм
        Movie selectedMovie = movieTable.getSelectionModel().getSelectedItem();

        if (selectedMovie == null) {
            showError("Будь ласка, виберіть фільм у таблиці, щоб переглянути акторів!");
            return;
        }

        try {
            // 1. Отримуємо список акторів із БД
            List<Actor> actors = dbService.getActorsByMovieId(selectedMovie.getId());

            // 2. Завантажуємо FXML файл
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/actors.fxml"));
            Parent root = loader.load();

            // 3. Отримуємо контролер нового вікна та передаємо дані
            ActorsController controller = loader.getController();
            controller.initData(selectedMovie, actors);

            // 4. Створюємо та показуємо нове вікно (Stage)
            Stage stage = new Stage();
            stage.setTitle("Список акторів: " + selectedMovie.getTitle());
            stage.initModality(Modality.APPLICATION_MODAL); // Робить вікно модальним
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            showError("Не вдалося відкрити вікно акторів: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Помилка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
