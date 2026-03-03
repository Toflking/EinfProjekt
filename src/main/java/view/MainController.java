package view;

import dao.AreaDAO;
import dao.CategoryDAO;
import javafx.animation.PauseTransition;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Area;
import model.Category;
import model.Meal;
import services.MealFilterService;

import java.sql.SQLException;
import java.util.List;
import java.util.function.Function;

public class MainController {

    @FXML private ListView<Meal> mealList;
    @FXML private TextField searchField;
    @FXML private ComboBox<Category> categoryFilter;
    @FXML private ComboBox<Area> areaFilter;

    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final AreaDAO areaDAO = new AreaDAO();

    private final MealFilterService filterService =
            new MealFilterService();

    private final PauseTransition debounce =
            new PauseTransition(Duration.millis(300));

    @FXML
    public void initialize() {

        refreshMealList();
        loadCategories();
        loadAreas();

        mealList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Meal meal, boolean empty) {
                super.updateItem(meal, empty);
                setText(empty || meal == null
                        ? null
                        : meal.getName());
            }
        });

        mealList.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {

                Meal selected =
                        mealList.getSelectionModel()
                                .getSelectedItem();

                if (selected != null) {
                    openMealDetail(selected);
                }
            }
        });

        debounce.setOnFinished(e -> runSearch());

        searchField.textProperty()
                .addListener((obs, o, n)
                        -> debounce.playFromStart());

        categoryFilter.setOnAction(e -> runSearch());
        areaFilter.setOnAction(e -> runSearch());
    }

    // LOAD CATEGORIES
    private void loadCategories() {
        try {
            loadFilter(
                    categoryFilter,
                    categoryDAO.listCategories(),
                    Category::getName
            );
        } catch (SQLException e) {
            showError("Category Error", e.getMessage());
        }
    }

    private void loadAreas() {
        try {
            loadFilter(
                    areaFilter,
                    areaDAO.listAreas(),
                    Area::getName
            );
        } catch (SQLException e) {
            showError("Area Error", e.getMessage());
        }
    }

    private <T> void loadFilter(
            ComboBox<T> comboBox,
            List<T> items,
            Function<T, String> nameExtractor
    ) {

        comboBox.getItems().clear();
        comboBox.getItems().add(null);
        comboBox.getItems().addAll(items);

        comboBox.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null
                        ? "All"
                        : nameExtractor.apply(item));
            }
        });

        comboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null
                        ? "All"
                        : nameExtractor.apply(item));
            }
        });
    }
    // SEARCH + FILTER
    @FXML
    private void runSearch() {

        String query = searchField.getText();
        Category selectedCategory =
                categoryFilter.getValue();
        Area selectedArea =
                areaFilter.getValue();

        Task<List<Meal>> task = new Task<>() {
            @Override
            protected List<Meal> call()
                    throws Exception {

                return filterService.searchMeals(
                        query,
                        selectedCategory,
                        selectedArea);
            }
        };

        task.setOnSucceeded(e ->
                mealList.getItems().setAll(
                        task.getValue()));

        task.setOnFailed(e ->
                showError("Search Error",
                        task.getException().getMessage())
        );

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    // REFRESH
    @FXML
    private void refreshMealList() {
        runSearch();
    }

    // NAVIGATION
    private void openMealDetail(Meal meal) {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/fxml/meal_detail.fxml"));

            Parent root = loader.load();

            MealDetailController controller =
                    loader.getController();

            controller.setMeal(meal);

            Stage stage =
                    (Stage) mealList
                            .getScene()
                            .getWindow();
            Scene scene = new Scene(root, 600, 800);

            scene.getStylesheets().add(
                    getClass().getResource("/css/style.css").toExternalForm()
            );

            stage.setScene(scene);
        } catch (Exception e) {
            showError("Navigation Error",
                    e.getMessage());
        }
    }

    // ERROR HANDLING
    private void showError(
            String title,
            String content) {

        Alert alert =
                new Alert(Alert.AlertType.ERROR);

        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}