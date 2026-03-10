package dao;

import db.Database;
import model.Meal;
import model.User;
import model.UserFavorite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Backend
// DAO steht für Data Access Object
// Alle Methoden für Interaktionen zwischen Meals Objekten und der Datenbank
/*
Jegliche Methoden hier funktionieren eigentlich nach demselben Prinzip:
1. Liste/Objekt erstellen, falls nötig
2. Verbindung mit der Datenbank aufnehmen
3. SQL query aufbauen (Unten immer PreparedStatement stmt)
4. Diesen Query ausführen, gibt ResultSet zurück
5. Resultset auswerten und entweder eine Liste/Objekt zurückgeben oder einen int, ob es funktioniert hat
 */
// Diese Klasse wurde vor dem Frontend gemacht, kann also Methoden beinhalten, die nie benutzt werden
public class UserFavoriteDAO {
    private static final String ADD_FAVORITE = "INSERT INTO user_favorites (user_id, meal_id) VALUES (?, ?)";
    private static final String REMOVE_FAVORITE = "DELETE FROM user_favorites WHERE user_id = ? AND meal_id = ?";
    private static final String IS_FAVORITE = "SELECT 1 FROM user_favorites WHERE user_id = ? AND meal_id = ?";

    // Methode zum Erstellen eines Favorites eines Users
    public int addFavorite(int user_id, int meal_id) throws SQLException {
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(ADD_FAVORITE)) {
            stmt.setInt(1, user_id);
            stmt.setInt(2, meal_id);
            return stmt.executeUpdate();
        }
    }

    // Überprüft, ob ein Meal ein Favorite von einem User ist
    public boolean isFavorite(int user_id, int meal_id) throws SQLException {
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(IS_FAVORITE)) {
            stmt.setInt(1, user_id);
            stmt.setInt(2, meal_id);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    // Methode zum Entfernen eines Favorites eines Users
    public int removeFavorite(int user_id, int meal_id) throws SQLException {
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(REMOVE_FAVORITE)) {
            stmt.setInt(1, user_id);
            stmt.setInt(2, meal_id);
            return stmt.executeUpdate();
        }
    }


}
