package view;

import dao.MealDAO;
import javafx.animation.PauseTransition;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Meal;

import java.sql.SQLException;
import java.util.List;

public class MainController {

    @FXML private ListView<Meal> mealList;
    @FXML private TextField searchField;

    private final MealDAO mealDAO = new MealDAO();
    private final Service service = new Service();
    private final PauseTransition debounce =
            new PauseTransition(Duration.millis(300));

    @FXML
    public void initialize() {

        refreshMealList();

        mealList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Meal meal, boolean empty) {
                super.updateItem(meal, empty);
                setText(empty || meal == null ? null : meal.getName());
            }
        });

        mealList.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {

                Meal selected =
                        mealList.getSelectionModel().getSelectedItem();

                if (selected != null) {
                    openMealDetail(selected);
                }
            }
        });

        debounce.setOnFinished(e -> runSearch());

        searchField.textProperty().addListener(
                (obs, oldV, newV) -> debounce.playFromStart()
        );
    }

    @FXML
    private void refreshMealList() {
        try {

            List<Meal> meals = mealDAO.listMeals();

            mealList.getItems().clear();
            mealList.getItems().addAll(meals);

        } catch (SQLException e) {
            showError("Database Error",
                    "Could not load meals: " + e.getMessage());
        }
    }

    @FXML
    private void runSearch() {

        String query = searchField.getText();

        Task<List<Meal>> task = new Task<>() {
            @Override
            protected List<Meal> call() throws Exception {
                return service.searchMeals(query);
            }
        };

        task.setOnSucceeded(e -> {
            mealList.getItems().clear();
            mealList.getItems().addAll(task.getValue());
        });

        task.setOnFailed(e ->
                showError("Error",
                        task.getException().getMessage())
        );

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    private void openMealDetail(Meal meal) {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/fxml/meal_detail.fxml"
                            )
                    );

            Parent root = loader.load();

            MealDetailController controller =
                    loader.getController();

            controller.setMeal(meal);

            Stage stage =
                    (Stage) mealList.getScene().getWindow();

            stage.setScene(new Scene(root));

        } catch (Exception e) {
            showError("Navigation Error", e.getMessage());
        }
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}