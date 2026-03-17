package view;

// DAO Klasse
import dao.MealDAO;
// FXML Klassen
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
// Model Klassen
import model.Meal;
import model.User;
// Für Exceptions
import java.io.IOException;
import java.sql.SQLException;

// Klasse für den Meal Manage Screen, verwaltet alles was dort vor sich geht
public class ManageMealsController {

    // FXML Felder
    @FXML private Label usernameLabel;
    @FXML private ListView<Meal> mealList;

    // DAO Objekt, zum Zugreifen auf das Backend
    private final MealDAO mealDAO = new MealDAO();

    // Aktiver User
    private User currentUser;

    // Methode wird nach dem Laden ausgeführt
    @FXML
    private void initialize() {
        // Cell Factory für die Liste
        mealList.setCellFactory(param -> new ListCell<>() {
            // Alle Elemente pro Element der Liste
            private final Label mealNameLabel = new Label();
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final Region spacer = new Region();
            private final HBox content = new HBox(10);

            {
                // Die Elemente setzen
                mealNameLabel.setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(spacer, Priority.ALWAYS);
                content.setAlignment(Pos.CENTER_LEFT);
                content.getChildren().addAll(
                        mealNameLabel,
                        spacer,
                        editButton,
                        deleteButton
                );

                // Wenn Edit Button benutzt wird
                editButton.setOnAction(event -> {
                    Meal meal = getItem();

                    if (meal != null) {
                        try {
                            // Meal Editor öffnen
                            openEditMeal(meal);
                        } catch (IOException e) {
                            showError("Navigation Error", e.getMessage());
                        }
                    }
                });

                // Wenn Delete Button benutzt wird
                deleteButton.setOnAction(event -> {
                    Meal meal = getItem();

                    if (meal != null) {
                        try {
                            // Meal löschen
                            mealDAO.deleteMealById(meal.getId());
                            loadMeals();
                        } catch (SQLException e) {
                            showError("Delete Error", e.getMessage());
                        }
                    }
                });
            }

            @Override
            protected void updateItem(Meal meal, boolean empty) {
                super.updateItem(meal, empty);

                if (empty || meal == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                mealNameLabel.setText(meal.getName());
                setText(null);
                setGraphic(content);
            }
        });
    }

    // Methode für das Mitgeben des Users
    public void setCurrentUser(User currentUser) {
        // User setzen
        this.currentUser = currentUser;
        // Username Label setzen oben rechts
        usernameLabel.setText(currentUser.getUsername());
        // eigene Meals laden
        loadMeals();
    }

    // Methode für das laden des Meals
    private void loadMeals() {
        if (currentUser == null) {
            return;
        }

        // Alle Meals des Users zur Liste hinzufügen
        try {
            mealList.getItems().setAll(
                    mealDAO.listMealsByUserId(currentUser.getId())
            );
        } catch (SQLException e) {
            showError("Meal Error", e.getMessage());
        }
    }

    // Methode für das Öffnen des Meal editors
    private void openEditMeal(Meal meal) throws IOException {
        // FXML laden
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/edit_meal.fxml"));
        Parent root = loader.load();

        // Meal und User an den Edit Controller weitergeben
        EditMealController controller = loader.getController();
        controller.setCurrentUser(currentUser);
        controller.setMeal(meal);

        // Stage setzen
        Stage stage = (Stage) mealList.getScene().getWindow();
        stage.getScene().setRoot(root);
    }

    // Methode um zum Main Screen zurückzugehen
    @FXML
    private void goBack() throws IOException {
        // FXML file laden
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main_view.fxml"));
        Parent root = loader.load();

        // User mitgeben
        MainController controller = loader.getController();
        controller.setCurrentUser(currentUser);

        // Scene laden
        Stage stage = (Stage) mealList.getScene().getWindow();
        stage.getScene().setRoot(root);
    }

    // Helper Methode, um Errors anzuzeigen
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
