package dao;

import db.Database;
import model.MealIngredient;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Backend
// DAO steht für Data Access Object
// Alle Methoden für Interaktionen zwischen MealIngredient Objekten und der Datenbank
/*
Jegliche Methoden hier funktionieren eigentlich nach demselben Prinzip:
1. Liste/Objekt erstellen, falls nötig
2. Verbindung mit der Datenbank aufnehmen
3. SQL query aufbauen (Unten immer PreparedStatement stmt)
4. Diesen Query ausführen, gibt ResultSet zurück
5. Resultset auswerten und entweder eine Liste/Objekt zurückgeben oder einen int, ob es funktioniert hat
 */
// Diese Klasse wurde vor dem Frontend gemacht, kann also Methoden beinhalten, die nie benutzt werden
public class MealIngredientDAO {
    // SQL Strings
    private static final String CREATE_MEAL_INGREDIENT =
            "INSERT INTO meal_ingredients (meal_id, ingredient_id, measure) VALUES (?, ?, ?)";
    private static final String DELETE_MEAL_INGREDIENTS_BY_MEAL_ID =
            "DELETE FROM meal_ingredients WHERE meal_id = ?";
    private static final String LIST_INGREDIENTS_BY_MEAL_ID =
            "SELECT mi.meal_id, mi.ingredient_id, " +
                    "i.name AS ingredient_name, mi.measure " +
                    "FROM meal_ingredients mi " +
                    "JOIN ingredients i ON mi.ingredient_id = i.id " +
                    "WHERE mi.meal_id = ?";

    // Erstellt ein MealIngredient
    public int createMealIngredient(MealIngredient mealIngredient) throws SQLException {
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(CREATE_MEAL_INGREDIENT)) {
            stmt.setInt(1, mealIngredient.getMeal_id());
            stmt.setInt(2, mealIngredient.getIngredient_id());
            stmt.setString(3, mealIngredient.getMeasure());
            return stmt.executeUpdate();
        }
    }

    // Löscht alle MealIngredients anhand einer Meal id
    public int deleteMealIngredientsByMealId(int mealId) throws SQLException {
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_MEAL_INGREDIENTS_BY_MEAL_ID)) {
            stmt.setInt(1, mealId);
            return stmt.executeUpdate();
        }
    }

    // Sucht nach Meal anhand der id in der DB und gibt alle Ingredients zurück die damit zusammen hängen
    public List<MealIngredient> listIngredientsByMealId(int mealId)
            throws SQLException {

        List<MealIngredient> mealIngredients = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(LIST_INGREDIENTS_BY_MEAL_ID)) {
            stmt.setInt(1, mealId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                mealIngredients.add(buildMealIngredient(rs));
            }
        }

        return mealIngredients;
    }

    // Helper Methoden, nur zum Organisieren des Codes, damit obige Methoden nicht zu gross sind
    // Baut ein MealIngredient Objekt anhand eines Result Sets auf
    private MealIngredient buildMealIngredient(ResultSet rs) throws SQLException {
        MealIngredient mealIngredient = new MealIngredient();
        mealIngredient.setMeal_id(rs.getInt("meal_id"));
        mealIngredient.setIngredient_id(rs.getInt("ingredient_id"));
        mealIngredient.setIngredient_name(rs.getString("ingredient_name"));
        mealIngredient.setMeasure(rs.getString("measure"));
        return mealIngredient;
    }
}
