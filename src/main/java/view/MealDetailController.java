package view;

// Dao Klassen
import dao.AreaDAO;
import dao.MealIngredientDAO;
import dao.UserFavoriteDAO;
// JavaFX Klassen
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
// Model und Service Klassen
import model.Meal;
import model.MealIngredient;
import model.User;
import services.IngredientCalculationService;
// Für Exceptions
import java.io.IOException;
import java.sql.SQLException;
// Lists werden hier für das Auflisten der Ingredients für das Meal benutzt
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// Die Klasse kontrolliert alles, was in der Meal Detail View angezeigt wird
public class MealDetailController {

    // Alle verschiedenen "Anzeigen" aus dem FXML file die im Meal Detail vorkommen
    @FXML private Label heart;
    @FXML private Label mealName;
    @FXML private ImageView mealImage;
    @FXML private Label mealCountry;
    @FXML private Label mealInstructions;
    @FXML private Label servingsLabel;
    @FXML private ListView<String> ingredientList;
    @FXML private Label videoTitle;
    @FXML private Label videoLabel;
    @FXML private Label sourceTitle;
    @FXML private Label sourceLabel;

    // Erstellen des DAO objekts zur Ausführung von Backend Methoden
    private final MealIngredientDAO mealIngredientDAO =
            new MealIngredientDAO();

    private final UserFavoriteDAO userFavoriteDAO =
            new UserFavoriteDAO();

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

    private boolean isFavorite = false;

    // Initizalize Methode, wird immer nach dem Laden des FXMl files ausgeführt
    @FXML
    private void initialize() {
        // Wenn mouse über das Herz hovered
        heart.setOnMouseEntered(event -> {
            // Wenn noch nicht favorite
            if (!isFavorite) {
                // Dann rote umrandung
                heart.getStyleClass().remove("heart-filled");
                if (!heart.getStyleClass().contains("heart-hover")) {
                    heart.getStyleClass().add("heart-hover");
                }
            }
        });

        // Wenn Mouse wieder weggeht
        heart.setOnMouseExited(event -> updateHeartState());

        // Wenn mouse klicked
        heart.setOnMouseClicked(event -> {
            if (currentUser == null || meal == null) {
                return;
            }

            try {
                // Wenn noch nicht Favorite
                if (!isFavorite) {
                    // Favorite hinzufügen
                    int result = userFavoriteDAO.addFavorite(currentUser.getId(), meal.getId());
                    // Wenn erfolgreich
                    if (result > 0) {
                        // Lokal auf true setzen
                        isFavorite = true;
                        // Herz updaten
                        updateHeartState();
                    } else {
                        showError("Could not add favorite.");
                    }

                    // Wenn Favorite
                } else {
                    // Favorite entfernen
                    int result = userFavoriteDAO.removeFavorite(currentUser.getId(), meal.getId());
                    // Wenn erfolgreich
                    if (result > 0) {
                        // Lokal auf false setzen
                        isFavorite = false;
                        // Herz updaten
                        updateHeartState();
                    } else {
                        showError("Could not remove favorite.");
                    }
                }

            } catch (SQLException e) {
                showError("Database error: " + e.getMessage());
            }
        });


    }


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
        // Video und Source link laden
        loadVideo();
        loadSource();
        // Zutaten Laden
        loadIngredients();
    }

    // Methode für das Laden der Zutaten
    private void loadIngredients() {

        try {
            baseIngredients =
                    // Methode aus dem MealIngredient DAO, dass beide tables joint
                    mealIngredientDAO.listIngredientsByMealId(meal.getId());
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

    // Methode zum laden des Video links
    private void loadVideo() {
        // überprüfen ob source vorhanden
        boolean hasVideo = meal.getYoutube() != null;

        videoTitle.setManaged(hasVideo);
        videoTitle.setVisible(hasVideo);
        videoLabel.setManaged(hasVideo);
        videoLabel.setVisible(hasVideo);

        // wenn leer, dann leer lassen
        if (!hasVideo) {
            videoLabel.setText("");
            return;
        }

        // sonst Video setzen
        videoLabel.setText(meal.getYoutube());
    }

    // Methode zum laden des Source links
    private void loadSource() {
        // überprüfen ob source vorhanden
        boolean hasSource = meal.getSource() != null;

        sourceTitle.setManaged(hasSource);
        sourceTitle.setVisible(hasSource);
        sourceLabel.setManaged(hasSource);
        sourceLabel.setVisible(hasSource);

        // wenn leer, dann leer lassen
        if (!hasSource) {
            sourceLabel.setText("");
            return;
        }
        // sonst source setzen
        sourceLabel.setText(meal.getSource());
    }


    // Methode für den Back Button, um wieder zum Main Screen zu kommen
    @FXML
    private void goBack() throws IOException {
        // FXML File laden
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main_view.fxml"));
        Parent root = loader.load();

        // User weitergeben
        MainController controller = loader.getController();
        controller.setCurrentUser(currentUser);
        // Scene setzen
        Stage stage = (Stage) mealName.getScene().getWindow();
        stage.getScene().setRoot(root);
    }

    // Methode zum Weitergeben des Users
    public void setCurrentUser(User currentUser) throws SQLException {
        // User setzen
        this.currentUser = currentUser;

        // Falls bereits favorite, lokal auch auf favorite setzen
        if (meal != null && userFavoriteDAO.isFavorite(currentUser.getId(), meal.getId())) {
            isFavorite = true;
        }

        updateHeartState();
    }

    // Herz korrekt Updaten
    private void updateHeartState() {
        // Je nachdem ob Favorite richtiges Herz benutzen
        heart.setText(isFavorite ? "♥" : "♡");
        heart.getStyleClass().remove("heart-filled");
        heart.getStyleClass().remove("heart-hover");

        // Wenn favorite dann style auf heart filled setzen
        if (isFavorite && !heart.getStyleClass().contains("heart-filled")) {
            heart.getStyleClass().add("heart-filled");
        }
    }

    // Helper Methode um mit Errors in der App anzuzeigen
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Favorite Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
