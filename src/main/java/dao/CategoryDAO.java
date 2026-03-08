package dao;

import db.Database;
import model.Category;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Backend
// DAO steht für Data Access Object
// Alle Methoden für Interaktionen zwischen Category Objekten und der Datenbank
/* 
Jegliche Methoden hier funktionieren eigentlich nach demselben Prinzip:
1. Liste/Objekt erstellen, falls nötig
2. Verbindung mit der Datenbank aufnehmen
3. SQL query aufbauen (Unten immer PreparedStatement stmt)
4. Diesen Query ausführen, gibt ResultSet zurück
5. Resultset auswerten und entweder eine Liste/Objekt zurückgeben oder einen int, ob es funktioniert hat
 */
// Diese Klasse wurde vor dem Frontend gemacht, kann also Methoden beinhalten, die nie benutzt werden
// Auf die Delete Methode wurde verzichtet, da es eigentlich keinen Grund gibt, eine Category zu löschen
public class CategoryDAO {
    // SQL Strings
    private static final String CREATE_CATEGORY = "INSERT INTO categories (name) VALUES (?)";
    private static final String GET_CATEGORY_BY_ID = "SELECT * FROM categories WHERE id = ?";
    private static final String GET_CATEGORY_BY_NAME = "SELECT * FROM categories WHERE name = ?";
    private static final String LIST_CATEGORIES = "SELECT * FROM categories";
    private static final String UPDATE_CATEGORY = "UPDATE categories SET name = ? WHERE id = ?";

    // Create Methode zum Erstellen einer Kategorie in der Datenbank
    public int createCategory(String name) throws SQLException {
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(CREATE_CATEGORY, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, name);
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : -1;
            }
        }
    }

    // Verschiedene Read Methoden:
    // Suche nach Category anhand der id in der DB
    public Category getCategoryById(int id) throws SQLException {
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_CATEGORY_BY_ID)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return buildCategory(rs);
                }
            }
        }
        return null;
    }

    // Suche nach Category anhand des Namens in der DB
    public Category getCategoryByName(String name) throws SQLException {
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_CATEGORY_BY_NAME)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return buildCategory(rs);
                }
            }
        }
        return null;
    }

    // Auflistung aller Categorys
    public List<Category> listCategories() throws SQLException {
        List<Category> categories = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(LIST_CATEGORIES);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                categories.add(buildCategory(rs));
            }
        }
        return categories;
    }

    // Update Methode, wenn man eine Category in der DB verändern möchte
    public boolean updateCategory(Category category) throws SQLException {
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_CATEGORY)) {
            stmt.setString(1, category.getName());
            stmt.setInt(2, category.getId());
            return stmt.executeUpdate() == 1;
        }
    }

    // Helper Methoden, nur zum Organisieren des Codes, damit obige Methoden nicht zu gross sind
    // Baut ein Category Objekt anhand eines Result Sets auf
    private Category buildCategory(ResultSet rs) throws SQLException {
        Category category = new Category();
        category.setId(rs.getInt("id"));
        category.setName(rs.getString("name"));
        return category;
    }
}

