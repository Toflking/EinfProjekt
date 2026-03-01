package view;

import dao.MealIngredientDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import model.Meal;
import model.MealIngredient;
import services.IngredientCalculationService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MealDetailController {

    @FXML private Label mealName;
    @FXML private Label servingsLabel;
    @FXML private ListView<String> ingredientList;

    private final MealIngredientDAO dao =
            new MealIngredientDAO();

    private final IngredientCalculationService calculationService =
            new IngredientCalculationService();

    private Meal meal;

    private List<MealIngredient> baseIngredients =
            new ArrayList<>();

    private int servings = 1;

    public void setMeal(Meal meal) {
        this.meal = meal;
        mealName.setText(meal.getName());
        loadIngredients();
    }

    private void loadIngredients() {

        try {
            baseIngredients =
                    dao.listIngredientsByMealId(meal.getId());

            updateIngredientView();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // SERVINGS BUTTONS
    @FXML
    private void increaseServings() {
        servings++;
        updateIngredientView();
    }

    @FXML
    private void decreaseServings() {
        if (servings > 1) {
            servings--;
            updateIngredientView();
        }
    }

    // UI UPDATE
    private void updateIngredientView() {

        servingsLabel.setText(String.valueOf(servings));

        ingredientList.getItems().clear();

        ingredientList.getItems().addAll(
                calculationService.scaleIngredients(
                        baseIngredients,
                        servings
                )
        );
    }

    // BACK BUTTON
    @FXML
    private void goBack() throws IOException {

        Parent root = FXMLLoader.load(
                getClass().getResource("/fxml/main_view.fxml"));

        Stage stage =
                (Stage) mealName.getScene().getWindow();

        stage.setScene(new Scene(root));
    }
}