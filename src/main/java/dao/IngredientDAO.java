package dao;

import db.Database;
import model.Ingredient;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Backend
// DAO steht für Data Access Object
// Alle Methoden für Interaktionen zwischen Ingredients Objekten und der Datenbank
/* 
Jegliche Methoden hier funktionieren eigentlich nach demselben Prinzip:
1. Liste/Objekt erstellen, falls nötig
2. Verbindung mit der Datenbank aufnehmen
3. SQL query aufbauen (Unten immer PreparedStatement stmt)
4. Diesen Query ausführen, gibt ResultSet zurück
5. Resultset auswerten und entweder eine Liste/Objekt zurückgeben oder einen int, ob es funktioniert hat
 */
// Diese Klasse wurde vor dem Frontend gemacht, kann also Methoden beinhalten, die nie benutzt werden
// Auf die Delete Methode wurde verzichtet, da es eigentlich keinen Grund gibt, ein Ingredient zu löschen
public class IngredientDAO {
    // SQL Strings
    private static final String CREATE_INGREDIENT = "INSERT INTO ingredients (name) VALUES (?)";
    private static final String GET_INGREDIENT_BY_ID = "SELECT * FROM ingredients WHERE id = ?";
    private static final String GET_INGREDIENT_BY_NAME = "SELECT * FROM ingredients WHERE name = ?";
    private static final String LIST_INGREDIENTS = "SELECT * FROM ingredients";
    private static final String UPDATE_INGREDIENT = "UPDATE ingredients SET name = ? WHERE id = ?";

    // Create Methode zum Erstellen eines Ingredients in der Datenbank
    public int createIngredient(String name) throws SQLException {
        try (Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(CREATE_INGREDIENT, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, name);
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : -1;
            }
        }
    }

    // Verschiedene Read Methoden:
    // Suche nach Ingredient anhand der id in der DB
    public Ingredient getIngredientById(int id) throws SQLException {
        try (Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(GET_INGREDIENT_BY_ID)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return buildIngredient(rs);
                }
            }
        }
        return null;
    }

    // Suche nach Ingredient anhand des Namens in der DB
    public Ingredient getIngredientByName(String name) throws SQLException {
        try (Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(GET_INGREDIENT_BY_NAME)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return buildIngredient(rs);
                }
            }
        }
        return null;
    }

    // Auflistung aller Ingredients
    public List<Ingredient> listIngredients() throws SQLException {
        List<Ingredient> ingredients = new ArrayList<>();
        try (Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(LIST_INGREDIENTS)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ingredients.add(buildIngredient(rs));
                }
            }
        }
        return ingredients;
    }
    
    // Update Methode, wenn man ein Ingredient in der DB verändern möchte
    public int updateIngredient(Ingredient ingredient) throws SQLException {
        try (Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(UPDATE_INGREDIENT)) {
            stmt.setString(1, ingredient.getName());
            stmt.setInt(2, ingredient.getId());
            return stmt.executeUpdate();
        }
    }

    // Helper Methoden, nur zum Organisieren des Codes, damit obige Methoden nicht zu gross sind
    // Baut ein Ingredient Objekt anhand eines Result Sets auf
    private Ingredient buildIngredient(ResultSet rs) throws SQLException {
        Ingredient ingredient = new Ingredient();
        ingredient.setId(rs.getInt("id"));
        ingredient.setName(rs.getString("name"));
        return ingredient;
    }
}
