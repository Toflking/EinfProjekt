package dao;

import db.Database;
import model.MealIngredient;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MealIngredientDAO {

    private static final String LIST_INGREDIENTS_BY_MEAL_ID =
            "SELECT mi.meal_id, mi.ingredient_id, " +
                    "i.name AS ingredient_name, mi.measure " +
                    "FROM meal_ingredients mi " +
                    "JOIN ingredients i ON mi.ingredient_id = i.id " +
                    "WHERE mi.meal_id = ?";

    public List<MealIngredient> listIngredientsByMealId(int mealId)
            throws SQLException {

        List<MealIngredient> mealIngredients = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt =
                     conn.prepareStatement(LIST_INGREDIENTS_BY_MEAL_ID)) {

            stmt.setInt(1, mealId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                mealIngredients.add(buildMealIngredient(rs));
            }
        }

        return mealIngredients;
    }

    private MealIngredient buildMealIngredient(ResultSet rs)
            throws SQLException {

        MealIngredient mealIngredient = new MealIngredient();

        mealIngredient.setMeal_id(
                rs.getInt("meal_id"));

        mealIngredient.setIngredient_id(
                rs.getInt("ingredient_id"));

        mealIngredient.setIngredient_name(
                rs.getString("ingredient_name"));

        mealIngredient.setMeasure(
                rs.getString("measure"));

        return mealIngredient;
    }
}