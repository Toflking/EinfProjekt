package view;

// DAO Klassen
import dao.AreaDAO;
import dao.CategoryDAO;
import dao.MealDAO;
import dao.UserFavoriteDAO;
// JavaFX Klassen
import javafx.animation.PauseTransition;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.util.Duration;
// Model Klassen
import model.Area;
import model.Category;
import model.Meal;
import model.User;
import services.MealFilterService;
// Für SQL Exceptions
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
// List brauchen wir immer für die Auflistung von einem unserer Objekte also z.B. Meals oder Ingredients
import java.util.List;
// Set brauchen wir hier für das Speichern aller Favoriten eines Users
import java.util.Objects;
import java.util.Set;
// Function um Anzeigetext aus Objekten zu bekommen
import java.util.function.Function;

// Hauptmethode für das Frontend, kontrolliert alle Vorgänge im Hauptfenster der App
public class MainController {

    // Felder in der App aus dem FXML File
    @FXML private Label usernameLabel;
    @FXML private CheckBox favoritesOnlyCheckBox;
    @FXML private ListView<Meal> mealList;
    @FXML private TextField searchField;
    @FXML private ComboBox<Category> categoryFilter;
    @FXML private ComboBox<Area> areaFilter;

    // Erstellen von Objekten
    // Dao Objekte für Ausführen von Methoden
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final AreaDAO areaDAO = new AreaDAO();
    private final MealDAO mealDAO = new MealDAO();
    private final UserFavoriteDAO userFavoriteDAO =
            new UserFavoriteDAO();

    // Zur Ausführung der Filter Methode
    private final MealFilterService filterService =
            new MealFilterService();

    // Zur Benutzung eines Debounces bei der Suche
    private final PauseTransition debounce =
            new PauseTransition(Duration.millis(300));

    // Aktiver Benutzer, wird durch Login/Registrierung gesetzt
    private User currentUser;
    // Set, dass alle Favoriten des Users speichert
    private final Set<Integer> favoriteMealIds =
            new HashSet<>();

    // initialize wird ähnlich wie main automatisch ausgeführt, jedoch erst, nachdem das fxml file geladen wurde
    @FXML
    public void initialize() {
        // Sachen laden
        // runSearch() Refreshed auch die Meal Liste, wird hier daher einfach dafür verwendet auch wenns eine "leere" Suche ist
        runSearch();
        loadCategories();
        loadAreas();

        // Für das saubere Anzeigen der Meals zusammen mit den Herzen
        mealList.setCellFactory(param -> new ListCell<>() {
            // Alle Elemente pro Zeile
            private final Label mealNameLabel = new Label();
            private final Label heartLabel = new Label("♡");
            private final Region spacer = new Region();
            private final HBox content = new HBox(10);

            {
                mealNameLabel.setMaxWidth(Double.MAX_VALUE);

                heartLabel.getStyleClass().add("meal-list-heart");
                // Event Skippen, wenn Mouse Pressed/Released auf dem Herz, damit man nicht ein Meal selected während man auf das Herz klickt
                heartLabel.setOnMousePressed(Event::consume);
                heartLabel.setOnMouseReleased(Event::consume);
                // Wenn die Mouse über das Herz geht
                heartLabel.setOnMouseEntered(event -> {
                    Meal currentMeal = getItem();

                    // Wenn bereits Favorite nichts ändern
                    if (currentMeal == null || favoriteMealIds.contains(currentMeal.getId())) {
                        return;
                    }

                    // Sonst Style auf hover setzen
                    heartLabel.getStyleClass().remove("heart-filled");
                    if (!heartLabel.getStyleClass().contains("heart-hover")) {
                        heartLabel.getStyleClass().add("heart-hover");
                    }
                });
                // Wenn Mouse das Herz Exited
                heartLabel.setOnMouseExited(event ->
                        // Hover entfernen
                        heartLabel.getStyleClass().remove("heart-hover"));
                // Wenn Mouse auf Herz Klickt
                heartLabel.setOnMouseClicked(event -> {
                    event.consume();

                    Meal currentMeal = getItem();

                    if (currentMeal == null || currentUser == null) {
                        return;
                    }

                    try {
                        // Wenn das Meal bereits Favorite ist, dann Favorite entfernen
                        if (favoriteMealIds.contains(currentMeal.getId())) {
                            int result = userFavoriteDAO.removeFavorite(
                                    currentUser.getId(),
                                    currentMeal.getId()
                            );

                            // =0 wenn gescheitert >0 wenn erfolgreich
                            if (result > 0) {
                                // Wenn erfolgreich auch lokal entfernen
                                favoriteMealIds.remove(currentMeal.getId());
                                updateHeartState(heartLabel, currentMeal);
                            } else {
                                showError("Favorite Error",
                                        "Could not remove favorite.");
                            }
                        } else {
                            // Ansonsten Favorite hinzufügen
                            int result = userFavoriteDAO.addFavorite(
                                    currentUser.getId(),
                                    currentMeal.getId()
                            );

                            // =0 wenn gescheitert >0 wenn erfolgreich
                            if (result > 0) {
                                // Wenn erfolgreich auch Lokal hinzufügen
                                favoriteMealIds.add(currentMeal.getId());
                                updateHeartState(heartLabel, currentMeal);
                            } else {
                                showError("Favorite Error",
                                        "Could not add favorite.");
                            }
                        }
                        // Liste updaten, falls nach Favorites gefiltert wird, werden so nicht mehr Favorisierte entfernt
                        runSearch();
                    } catch (SQLException e) {
                        showError("Favorite Error", e.getMessage());
                    }
                });
                // HBox setzen
                HBox.setHgrow(spacer, Priority.ALWAYS);
                content.setAlignment(Pos.CENTER_LEFT);
                content.getChildren().addAll(
                        mealNameLabel,
                        spacer,
                        heartLabel
                );
            }

            // Für das Korrekte updaten der Meals
            @Override
            protected void updateItem(Meal meal, boolean empty) {
                super.updateItem(meal, empty);

                if (empty || meal == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                mealNameLabel.setText(meal.getName());
                updateHeartState(heartLabel, meal);

                setText(null);
                setGraphic(content);
            }
        });

        // Mouse Clicks tracken
        mealList.setOnMouseClicked(event -> {
            // Wenn zwei Clicks
            if (event.getClickCount() == 2) {

                // Das Meal Finden, dass gerade angeklickt wurde
                Meal selected =
                        mealList.getSelectionModel()
                                .getSelectedItem();

                // Meal Detail View öffnen
                if (selected != null) {
                    openMealDetail(selected);
                }
            }
        });

        // Debounce für die Search damit nicht zu viele Searches ausgeführt werden
        debounce.setOnFinished(e -> runSearch());
        searchField.textProperty()
                .addListener((obs, o, n)
                        -> debounce.playFromStart());

        // Search ausführen, wenn einer der Filter gesetzt wurde
        categoryFilter.setOnAction(e -> runSearch());
        areaFilter.setOnAction(e -> runSearch());
        favoritesOnlyCheckBox.setOnAction(e -> runSearch());
    }

    // Kategorien laden
    private void loadCategories() {
        try {
            // Filtern
            loadFilter(
                    categoryFilter,
                    categoryDAO.listCategories(),
                    Category::getName
            );
        } catch (SQLException e) {
            showError("Category Error", e.getMessage());
        }
    }

    // Areas laden
    private void loadAreas() {
        try {
            // Filtern
            loadFilter(
                    areaFilter,
                    areaDAO.listAreas(),
                    Area::getName
            );
        } catch (SQLException e) {
            showError("Area Error", e.getMessage());
        }
    }

    // Filtern
    private <T> void loadFilter(
            ComboBox<T> comboBox,
            List<T> items,
            Function<T, String> nameExtractor
    ) {

        comboBox.getItems().clear();
        comboBox.getItems().add(null);
        comboBox.getItems().addAll(items);

        // Sorgt dafür, wie die Items im Dropdown Menu aussehen
        comboBox.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null
                        ? "All"
                        : nameExtractor.apply(item));
            }
        });

        // Sorgt dafür wie das aktuell ausgewählte Item aussieht
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
    // Methode für Suche und Filtern der Meal List
    @FXML
    private void runSearch() {

        // Text aus dem Eingabefeld nehmen
        String query = searchField.getText();
        // Category und Area Filter entnehmen
        Category selectedCategory =
                categoryFilter.getValue();
        Area selectedArea =
                areaFilter.getValue();

        boolean favoritesOnly = favoritesOnlyCheckBox.isSelected();

        // Task erstellen
        Task<List<Meal>> task = new Task<>() {
            @Override
            protected List<Meal> call()
                    throws Exception {
                // Nach Query, Category und Area Filtern
                return filterService.searchMeals(
                        query,
                        selectedCategory,
                        selectedArea,
                        favoritesOnly,
                        favoriteMealIds);
            }
        };


        task.setOnSucceeded(e ->
                // Für alle Meals die Namen in die Liste eintragen
                mealList.getItems().setAll(
                        task.getValue()));

        task.setOnFailed(e ->
                showError("Search Error",
                        task.getException().getMessage())
        );
        // thread erstellen mit der task
        Thread thread = new Thread(task);
        // Damit die App sauber schliesst, auch wenn eine Task noch am Laufen ist
        thread.setDaemon(true);
        thread.start();
    }

    // Wird ausgeführt, wenn man ein Meal doppelklickt, öffnet die Detailansicht
    private void openMealDetail(Meal meal) {

        try {
            // FXML laden
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/meal_detail.fxml"));
            Parent root = loader.load();

            // Controller erstellen
            MealDetailController controller =
                    loader.getController();

            controller.setMeal(meal);
            controller.setCurrentUser(currentUser);

            // Stage erstellen
            Stage stage = (Stage) mealList.getScene().getWindow();
            Scene scene = new Scene(root, 600, 800);

            // CSS file laden
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/style.css")).toExternalForm());

            // Scene setzen
            stage.setScene(scene);
        } catch (Exception e) {
            showError("Navigation Error",
                    e.getMessage());
        }
    }

    // Methode zum Setzen des Users, wird beim Login/Registrieren gebraucht um den User an den MainController mitzugeben
    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
        // Username Label setzen
        usernameLabel.setText(currentUser.getUsername());
        // Favoriten laden
        loadFavoritesForCurrentUser();
        // Liste refreshen
        mealList.refresh();
    }

    // Favoriten des Useres laden
    private void loadFavoritesForCurrentUser() {
        // Set Clearen
        favoriteMealIds.clear();

        if (currentUser == null) {
            return;
        }

        // alle Favorites des Users holen und zum Set hinzufügen
        try {
            for (Meal meal : mealDAO
                    .getFavoriteMealsByUser(currentUser.getId())) {
                favoriteMealIds.add(meal.getId());
            }
        } catch (SQLException e) {
            showError("Favorite Error", e.getMessage());
        }
    }

    // Herz korrekt updaten
    private void updateHeartState(Label heartLabel, Meal meal) {
        // Je nachdem ob Favorite richtiges Herz benutzen
        heartLabel.setText(favoriteMealIds.contains(meal.getId()) ? "♥" : "♡");

        heartLabel.getStyleClass().remove("heart-filled");
        heartLabel.getStyleClass().remove("heart-hover");

        // Wenn favorite dann style auf heart filled setzen

        if (favoriteMealIds.contains(meal.getId()) && !heartLabel.getStyleClass().contains("heart-filled")) {
            heartLabel.getStyleClass().add("heart-filled");
        }
    }

    // Methode für das Ausloggen des Users, geht zum Loginscreen zurück
    public void logout() throws IOException {
        // User unsetten
        this.currentUser = null;
        favoriteMealIds.clear();

        // FXMl laden
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Parent root = loader.load();

        // Scene setzen
        Stage stage = (Stage) this.mealList.getScene().getWindow();
        stage.getScene().setRoot(root);
    }

    // Methode um Settings Screen zu Öffnen
    public void openSettings() throws IOException {
        // FXML laden
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/settings.fxml"));
        Parent root = loader.load();

        // User Mitgeben
        SettingsController controller = loader.getController();
        controller.setCurrentUser(currentUser);

        // Scene setzen
        Stage stage = (Stage) this.mealList.getScene().getWindow();
        stage.getScene().setRoot(root);
    }

    // Methode die den Meal Ersteller Screen öffnet
    public void openCreateMeal() throws IOException {
        // FXML laden
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/create_meal.fxml"));
        Parent root = loader.load();

        // User Mitgeben
        CreateMealController controller = loader.getController();
        controller.setCurrentUser(currentUser);

        // Scene setzen
        Stage stage = (Stage) this.mealList.getScene().getWindow();
        stage.getScene().setRoot(root);
    }

    public void openManageMeals() throws IOException {
        // FXML laden
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/manage_meals.fxml"));
        Parent root = loader.load();

        // User Mitgeben
        ManageMealsController controller = loader.getController();
        controller.setCurrentUser(currentUser);

        // Scene setzen
        Stage stage = (Stage) this.mealList.getScene().getWindow();
        stage.getScene().setRoot(root);
    }

    // helper methode um errors in der app anzuzeigen
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
