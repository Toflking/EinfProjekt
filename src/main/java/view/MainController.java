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
// Unsere Klassen
import model.Area;
import model.Category;
import model.Meal;
import services.MealFilterService;
// Für SQL Exceptions
import java.sql.SQLException;
// List brauchen wir immer für die Auflistung von einem unserer Objekte also z.B. Meals oder Ingredients
import java.util.List;
// Function um Anzeigetext aus Objekten zu bekommen
import java.util.function.Function;

// Hauptmethode für das Frontend, kontrolliert alle Vorgänge im Hauptfenster der App
public class MainController {

    // Felder in der App aus dem FXML File
    @FXML private ListView<Meal> mealList;
    @FXML private TextField searchField;
    @FXML private ComboBox<Category> categoryFilter;
    @FXML private ComboBox<Area> areaFilter;

    // Erstellen von Objekten
    // Dao Objekte für Ausführen von Methoden
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final AreaDAO areaDAO = new AreaDAO();

    // Zur Ausführung der Filter Methode
    private final MealFilterService filterService =
            new MealFilterService();

    // Zur Benutzung eines Debounces bei der Suche
    private final PauseTransition debounce =
            new PauseTransition(Duration.millis(300));

    // initialize wird ähnlich wie main automatisch ausgeführt, jedoch erst, nachdem das fxml file geladen wurde
    @FXML
    public void initialize() {
        // Sachen laden
        // runSearch() Refreshed auch die Meal Liste, wird hier daher einfach dafür verwendet auch wenns eine "leere" Suche ist
        runSearch();
        loadCategories();
        loadAreas();

        // Für das saubere Anzeigen der Meals
        mealList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Meal meal, boolean empty) {
                super.updateItem(meal, empty);
                setText(empty || meal == null
                        ? null
                        : meal.getName());
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

        // Task erstellen
        Task<List<Meal>> task = new Task<>() {
            @Override
            protected List<Meal> call()
                    throws Exception {
                // Nach Query, Category und Area Filtern
                return filterService.searchMeals(
                        query,
                        selectedCategory,
                        selectedArea);
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
            // Detailansicht FXML laden
            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/fxml/meal_detail.fxml"));

            Parent root = loader.load();

            // Controller erstellen
            MealDetailController controller =
                    loader.getController();

            controller.setMeal(meal);

            // Stage erstellen
            Stage stage =
                    (Stage) mealList
                            .getScene()
                            .getWindow();
            Scene scene = new Scene(root, 600, 800);

            // CSS file laden
            scene.getStylesheets().add(
                    getClass().getResource("/css/style.css").toExternalForm()
            );

            // Stage setzen
            stage.setScene(scene);
        } catch (Exception e) {
            showError("Navigation Error",
                    e.getMessage());
        }
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