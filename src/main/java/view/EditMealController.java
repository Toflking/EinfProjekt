package view;
// Dao Klassen
import dao.AreaDAO;
import dao.CategoryDAO;
import dao.IngredientDAO;
import dao.MealDAO;
import dao.MealIngredientDAO;
// Java FX Klassen
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
// Alle Elemente
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
// Model Klassen
import model.Area;
import model.Category;
import model.Ingredient;
import model.Meal;
import model.MealIngredient;
import model.User;
// Für Exceptions
import java.io.IOException;
import java.sql.SQLException;
// Listen hier für die Auflistung aller Areas/Categorys/Ingredients in den Comboxen benötigt, sowie für das Festhalten der MealIngredients
import java.util.ArrayList;
import java.util.List;

// Methode für den Edit Meal Screen, verwaltet alles, was dort vor sich geht
public class EditMealController {

    // FXML Felder aus dem FXML file
    @FXML private ScrollPane root;
    @FXML private Label editMealStatusLabel;
    @FXML private TextField mealNameField;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private ComboBox<String> areaComboBox;
    @FXML private TextField thumbField;
    @FXML private TextField youtubeField;
    @FXML private TextField sourceField;
    @FXML private TextField tagsField;
    @FXML private TextArea instructionsArea;
    @FXML private VBox ingredientRowsBox;

    // DAO Objekte für Backend access
    private final MealDAO mealDAO = new MealDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final AreaDAO areaDAO = new AreaDAO();
    private final IngredientDAO ingredientDAO = new IngredientDAO();
    private final MealIngredientDAO mealIngredientDAO = new MealIngredientDAO();

    // Aktiver User, wird durch MainController weitergegeben
    private User currentUser;
    private Meal meal;
    // Listen für die Comboboxen
    private final List<String> allIngredientNames = new ArrayList<>();
    private final List<String> allCategoryNames = new ArrayList<>();
    private final List<String> allAreaNames = new ArrayList<>();

    // Wird nach laden des FXML files ausgeführt
    @FXML
    private void initialize() {
        // Comboboxen laden
        loadComboBoxData();
        configureFilterableComboBox(categoryComboBox, allCategoryNames);
        configureFilterableComboBox(areaComboBox, allAreaNames);

        // Ingredients Rows setuppen
        for (Node node : ingredientRowsBox.getChildren()) {
            if (node instanceof HBox row) {
                configureIngredientRow(row);
            }
        }
    }

    // Ingredient Rows hinzufügen
    // Wird durch den + Button ausgelöst
    @FXML
    private void addIngredientRow(ActionEvent event) {
        // Neue HBox erstellen
        HBox newRow = createIngredientRow();
        Object source = event.getSource();

        // Schauen, ob durch Button ausgelöst und Row Index holen
        if (source instanceof Button button
                && button.getParent() instanceof HBox currentRow) {
            int rowIndex = ingredientRowsBox.getChildren().indexOf(currentRow);
            // Row hinzufügen, eins unter der momentanen Row
            ingredientRowsBox.getChildren().add(rowIndex + 1, newRow);
            return;
        }

        ingredientRowsBox.getChildren().add(newRow);
    }

    // Ingredient Row entfernen
    // Wird durch den - Button ausgelöst
    @FXML
    private void removeIngredientRow(ActionEvent event) {
        Object source = event.getSource();

        // Sicherstellen, dass durch Button ausgelöst
        if (!(source instanceof Button button)
                || !(button.getParent() instanceof HBox currentRow)) {
            return;
        }

        // Falls nur eine Row da, dann Row clearen nicht deleten
        if (ingredientRowsBox.getChildren().size() > 1) {
            ingredientRowsBox.getChildren().remove(currentRow);
            return;
        }

        // Row Deleten
        for (Node child : currentRow.getChildren()) {
            if (child instanceof ComboBox<?> combo) {
                combo.getSelectionModel().clearSelection();
                combo.getEditor().clear();
            } else if (child instanceof TextField field) {
                field.clear();
            }
        }
    }

    // Speichern des Meals, wird durch den Button ausgelöst
    @FXML
    private void saveMeal() throws SQLException, IOException {
        // Alle Variablen für ein Meal Objekt erstellen und aus den Feldern Parsen
        String name = safeTrim(mealNameField);
        String category = safeTrim(categoryComboBox.getEditor());
        String area = safeTrim(areaComboBox.getEditor());
        String thumb = safeTrim(thumbField);
        String youtube = safeTrim(youtubeField);
        String source = safeTrim(sourceField);
        String tags = safeTrim(tagsField);
        String instructions = safeTrim(instructionsArea);
        // Liste erstellen für die MealIngredients
        List<MealIngredient> ingredients = new ArrayList<>();

        // Alle Ingredient Rows durchgehen
        for (Node node : ingredientRowsBox.getChildren()) {
            if (!(node instanceof HBox row)) {
                continue;
            }

            ComboBox<String> ingredientBox = null;
            TextField measureField = null;

            // Sichergehen dass die row beide Felder beinhält
            for (Node child : row.getChildren()) {
                if (child instanceof ComboBox<?> combo) {
                    ingredientBox = (ComboBox<String>) combo;
                } else if (child instanceof TextField field) {
                    measureField = field;
                }
            }

            // Überspringt nodes, die keine gültige Ingredient Row ist
            if (ingredientBox == null || measureField == null) {
                continue;
            }

            // Felder Parsen
            String ingredientName = ingredientBox.getEditor().getText().trim();
            String measure = measureField.getText().trim();

            // Wenn der Name leer ist überspringen
            if (ingredientName.isEmpty()) {
                continue;
            }

            // Meal Ingredient bauen, hier noch ohne meal und ingredient id, diese werden später gesetzt
            MealIngredient mealIngredient = new MealIngredient();
            mealIngredient.setIngredient_name(ingredientName);
            mealIngredient.setMeasure(measure.isBlank() ? null : measure);
            // zur Liste hinzufügen
            ingredients.add(mealIngredient);
        }

        // Sichergehen, dass Name und Instruktionen vorhanden, haben entschieden, dass das die einzigen Pflichfelder sind
        if (name.isBlank() || instructions.isBlank()) {
            editMealStatusLabel.setText("Name and instructions cannot be empty");
            editMealStatusLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        // Schauen, dass mindestens eine Zutat da ist
        if (ingredients.isEmpty()) {
            editMealStatusLabel.setText("There needs to be at least one ingredient");
            editMealStatusLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        // Überprüfen, dass der Name unique ist
        Meal existingMeal = mealDAO.getMealByName(name);
        if (existingMeal != null && existingMeal.getId() != meal.getId()) {
            editMealStatusLabel.setText("Meal already exists");
            editMealStatusLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        // Sollte eine Kategorie ins Feld eingegeben worden sein, welche noch nicht existiert dann eine neue Kategorie erstellen
        if (!category.isBlank()) {
            if (categoryDAO.getCategoryByName(category) == null) {
                categoryDAO.createCategory(category);
            }
            // Kategorie setzen
            meal.setCategory_id(categoryDAO.getCategoryByName(category).getId());
        } else {
            meal.setCategory_id(null);
        }

        // Sollte eine Area ins Feld eingegeben worden sein, welche noch nicht existiert dann eine neue Area erstellen
        if (!area.isBlank()) {
            if (areaDAO.getAreaByName(area) == null) {
                areaDAO.createArea(area);
            }
            // Area setzen
            meal.setArea_id(areaDAO.getAreaByName(area).getId());
        } else {
            meal.setArea_id(null);
        }

        // restliche variablen von meal setzen
        meal.setName(name);
        meal.setInstructions(instructions);
        meal.setThumb(thumb.isBlank() ? null : thumb);
        meal.setYoutube(youtube.isBlank() ? null : youtube);
        meal.setSource(source.isBlank() ? null : source);
        meal.setTags(tags.isBlank() ? null : tags);

        // Meal updaten und id speichern
        int result = mealDAO.updateMeal(meal);
        // Wenn erfolgreich (Bei nicht erfolg id = 0)
        if (result > 0) {
            // Alle dazugehörten MealIngredients löschen
            mealIngredientDAO.deleteMealIngredientsByMealId(meal.getId());
            // für alle Ingredients ein Meal Ingredient erstellen
            for (MealIngredient mealIngredient : ingredients) {
                // Wenn das eingegebene Ingredient noch nicht existiert dann ein neues erstellen
                if (ingredientDAO.getIngredientByName(mealIngredient.getIngredient_name()) == null) {
                    ingredientDAO.createIngredient(mealIngredient.getIngredient_name());
                }
                // Meal id setzen
                mealIngredient.setMeal_id(meal.getId());
                // Ingredient id setzen
                mealIngredient.setIngredient_id(
                        ingredientDAO.getIngredientByName(mealIngredient.getIngredient_name()).getId()
                );
                // MealIngredient erstellen (name und measure wurden ja oben bereits gesetzt)
                mealIngredientDAO.createMealIngredient(mealIngredient);
            }

            // Zurück zum main screen
            goBack();
        }
    }

    // Methode zum Zurückgehen, wird durch den Back Button und durch den Create Meal button ausgelöst
    @FXML
    private void goBack() throws IOException {
        // FXML file laden
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/manage_meals.fxml"));
        Parent root = loader.load();

        // User mitgeben
        ManageMealsController controller = loader.getController();
        controller.setCurrentUser(currentUser);

        // Scene setzen
        Stage stage = (Stage) this.root.getScene().getWindow();
        stage.getScene().setRoot(root);
    }

    // Damit der Main Controller den User mitgeben kann
    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    // Für das Mitgeben des Meals vom Manager and den Editer
    public void setMeal(Meal meal) {
        // Meal setzen
        this.meal = meal;

        // Felder setzen
        mealNameField.setText(valueOrEmpty(meal.getName()));
        thumbField.setText(valueOrEmpty(meal.getThumb()));
        youtubeField.setText(valueOrEmpty(meal.getYoutube()));
        sourceField.setText(valueOrEmpty(meal.getSource()));
        tagsField.setText(valueOrEmpty(meal.getTags()));
        instructionsArea.setText(valueOrEmpty(meal.getInstructions()));

        // Comboboxen setzen
        try {
            if (meal.getCategory_id() != null) {
                categoryComboBox.getEditor().setText(
                        categoryDAO.getCategoryById(meal.getCategory_id()).getName()
                );
            }

            if (meal.getArea_id() != null) {
                areaComboBox.getEditor().setText(
                        areaDAO.getAreaById(meal.getArea_id()).getName()
                );
            }

            // Ingredients Rows Clearen
            ingredientRowsBox.getChildren().clear();
            List<MealIngredient> mealIngredients =
                    mealIngredientDAO.listIngredientsByMealId(meal.getId());

            if (mealIngredients.isEmpty()) {
                ingredientRowsBox.getChildren().add(createIngredientRow());
                return;
            }
            // Ingredient Rows setzen
            for (MealIngredient mealIngredient : mealIngredients) {
                ingredientRowsBox.getChildren().add(
                        createIngredientRow(mealIngredient.getIngredient_name(), mealIngredient.getMeasure())
                );
            }
        } catch (SQLException e) {
            editMealStatusLabel.setText("Could not load meal data");
            editMealStatusLabel.setStyle("-fx-text-fill: red;");
        }
    }

    // Zum Erstellen weiterer Zeilen mit Ingredients/Measures
    private HBox createIngredientRow() {
        return createIngredientRow("", "");
    }

    private HBox createIngredientRow(String ingredientName, String measure) {
        // Combobox fürs Ingredient
        ComboBox<String> ingredientBox = new ComboBox<>();
        ingredientBox.setEditable(true);
        ingredientBox.setPromptText("Ingredient");
        ingredientBox.setPrefWidth(250);
        ingredientBox.getEditor().setText(ingredientName == null ? "" : ingredientName);

        // Textfield fürs measure
        TextField measureField = new TextField();
        measureField.setPromptText("Measure");
        measureField.setPrefWidth(160);
        measureField.setText(measure == null ? "" : measure);

        // + Button für neue Zeile
        Button addButton = new Button("+");
        addButton.setOnAction(this::addIngredientRow);

        // - Button um Zeile zu löschen
        Button removeButton = new Button("-");
        removeButton.setOnAction(this::removeIngredientRow);

        // Hbox erstellen
        HBox row = new HBox(10);
        // Alle Elemente in der HBox erstellen
        row.getChildren().addAll(
                ingredientBox,
                measureField,
                addButton,
                removeButton
        );

        configureIngredientRow(row);
        return row;
    }

    // Methode zum Konfigurieren der Elemente in der HBOX
    private void configureIngredientRow(HBox row) {
        // Für alle Elemente in der HBox
        for (Node child : row.getChildren()) {
            // Falls Combobox (Für Ingredients)
            if (child instanceof ComboBox<?> combo) {
                // Combobox erstellen und laden
                ComboBox<String> ingredientBox = (ComboBox<String>) combo;
                configureFilterableComboBox(ingredientBox, allIngredientNames);
                // Falls Button
            } else if (child instanceof Button button) {
                // Button onAction definieren
                if ("+".equals(button.getText())) {
                    button.setOnAction(this::addIngredientRow);
                } else if ("-".equals(button.getText())) {
                    button.setOnAction(this::removeIngredientRow);
                }
            }
        }
    }

    // Methode zum laden aller Ingredients/Areas/Categorys für die Comboboxen
    private void loadComboBoxData() {
        // Comboboxen leeren
        allIngredientNames.clear();
        allCategoryNames.clear();
        allAreaNames.clear();

        // Alle Ingredients/Areas/Categorys aus der DB holen und zu den Listen hinzufügen
        try {
            for (Ingredient ingredient : ingredientDAO.listIngredients()) {
                allIngredientNames.add(ingredient.getName());
            }

            for (Category category : categoryDAO.listCategories()) {
                allCategoryNames.add(category.getName());
            }

            for (Area area : areaDAO.listAreas()) {
                allAreaNames.add(area.getName());
            }
        } catch (SQLException e) {
            editMealStatusLabel.setText("Could not load form data");
            editMealStatusLabel.setStyle("-fx-text-fill: red;");
        }
    }

    // Methode zum Filtern der Comboboxen zu dem was im Feld steht
    private void configureFilterableComboBox(
            ComboBox<String> comboBox,
            List<String> allItems) {
        // Combobox Items setzen
        comboBox.getItems().setAll(allItems);
        // Listener für Text in der Combobox
        comboBox.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            // Query auslesen
            String query = newValue == null ? "" : newValue.trim().toLowerCase();

            if (query.isEmpty()) {
                comboBox.getItems().setAll(allItems);
            } else {
                // Wenn nicht empty dann filtern
                comboBox.getItems().setAll(
                        allItems.stream()
                                .filter(item -> item.toLowerCase().contains(query))
                                .toList()
                );
            }

            comboBox.show();
        });
    }

    // Zum Trimmen von Strings
    private String safeTrim(TextField textField) {
        return textField.getText() == null
                ? ""
                : textField.getText().trim();
    }

    private String safeTrim(TextArea textArea) {
        return textArea.getText() == null
                ? ""
                : textArea.getText().trim();
    }

    private String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }
}
