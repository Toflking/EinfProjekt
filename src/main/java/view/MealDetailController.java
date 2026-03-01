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

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class MealDetailController {

    @FXML private Label mealName;
    @FXML private ListView<String> ingredientList;

    private final MealIngredientDAO dao =
            new MealIngredientDAO();

    private Meal meal;

    public void setMeal(Meal meal) {
        this.meal = meal;
        loadIngredients();
    }

    private void loadIngredients() {

        mealName.setText(meal.getName());
        ingredientList.getItems().clear();

        try {

            List<MealIngredient> ingredients =
                    dao.listIngredientsByMealId(meal.getId());

            for (MealIngredient mi : ingredients) {

                ingredientList.getItems().add(
                        mi.getMeasure() + " - "
                                + mi.getIngredient_name()
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goBack() throws IOException {

        Parent root = FXMLLoader.load(
                getClass().getResource("/fxml/main_view.fxml"));

        Stage stage =
                (Stage) mealName.getScene().getWindow();

        stage.setScene(new Scene(root));
    }
}