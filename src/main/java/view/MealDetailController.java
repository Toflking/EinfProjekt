package view;

import dao.AreaDAO;
import dao.MealIngredientDAO;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import model.Meal;
import model.MealIngredient;
import services.IngredientCalculationService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import model.User;

// Die Klasse kontrolliert alles, was in der Meal Detail View angezeigt wird
public class MealDetailController {

    // Alle verschiedenen "Anzeigen" aus dem FXML file die im Meal Detail vorkommen
    @FXML private Label mealName;
    @FXML private ImageView mealImage;
    @FXML private Label mealCountry;
    @FXML private Label mealInstructions;
    @FXML private Label servingsLabel;
    @FXML private ListView<String> ingredientList;

    // Erstellen des DAO objekts zur Ausführung von DAO Methoden
    private final MealIngredientDAO dao =
            new MealIngredientDAO();

    // Erstellen des Service Klassen Objekts zur Berechnung der Mengen
    private final IngredientCalculationService calculationService =
            new IngredientCalculationService();

    // Speichern welches Meal geladen ist
    private Meal meal;
    private User currentUser;

    // Liste für das Berechnen der Measures. Behält immer die Mengen ohne Skalierung
    private List<MealIngredient> baseIngredients =
            new ArrayList<>();

    // Für die Anzahl Personen zur Berechnung der Menge der Zutaten
    private int servings = 1;

    // Setzen des Meals, das angezeigt werden soll, wird einmal ausgeführt, wenn Meal Details View aufgemacht wird
    public void setMeal(Meal meal) throws SQLException {
        // Überschreiben des Feldes
        this.meal = meal;
        // Titel setzen
        mealName.setText(meal.getName());
        Image image;
        // Bild Laden
        try {
            image = new Image(meal.getThumb(), true);
        } catch (Exception e) {
            image = new Image(Objects.requireNonNull(getClass().getResource("/images/placeholder.png")).toExternalForm());
        }
        // Bild setzen
        mealImage.setImage(image);
        AreaDAO areaDAO = new AreaDAO();
        // Text für das Land setzen
        mealCountry.setText("This Meal is " + areaDAO.getAreaById(meal.getArea_id()).getName());
        // Meal Instruktionen setzen
        mealInstructions.setText(meal.getInstructions());
        // Zutaten Laden
        loadIngredients();
    }

    // Methode für das Laden der Zutaten
    private void loadIngredients() {

        try {
            baseIngredients =
                    // Methode aus dem MealIngredient DAO, dass beide tables joint
                    dao.listIngredientsByMealId(meal.getId());
            // Update des UI
            updateIngredientView();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Methode für den Servings Button +
    @FXML
    private void increaseServings() {
        servings++;
        updateIngredientView();
    }
    // Methode für den Servings Button -
    @FXML
    private void decreaseServings() {
        // Nur Zahl verringern wenn über 1
        if (servings > 1) {
            servings--;
            updateIngredientView();
        }
    }

    // Methode für das Updaten des UI
    private void updateIngredientView() {

        // Zahl bei Servings setzen
        servingsLabel.setText(String.valueOf(servings));

        // Measures Liste resetten
        ingredientList.getItems().clear();
        ingredientList.setFixedCellSize(24);

        // Grösse der Liste anhand Anzahl Zutaten setzen
        ingredientList.prefHeightProperty().bind(
                ingredientList.fixedCellSizeProperty()
                        .multiply(Bindings.size(ingredientList.getItems()))
                        .add(2)
        );
        ingredientList.minHeightProperty().bind(ingredientList.prefHeightProperty());
        ingredientList.maxHeightProperty().bind(ingredientList.prefHeightProperty());
        // Measures Skalieren nach Personen
        ingredientList.getItems().addAll(
                calculationService.scaleIngredients(
                        baseIngredients,
                        servings
                )
        );
    }


    // Methode für den Back Button, um wieder zum Main Screen zu kommen
    @FXML
    private void goBack() throws IOException {
        // FXML File laden
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main_view.fxml"));
        Parent root = loader.load();

        MainController controller = loader.getController();
        controller.setCurrentUser(currentUser);
        // Scene setzen
        Stage stage = (Stage) mealName.getScene().getWindow();
        stage.getScene().setRoot(root);
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
}
