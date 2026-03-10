package dao;

import db.Database;
import model.User;
// Zum hashen der Passwörter
import org.mindrot.jbcrypt.BCrypt;

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
public class UserDAO {
    private static final String CREATE_USER = "INSERT INTO users (username, password) VALUES (?, ?)";
    private static final String GET_USER_BY_ID = "SELECT * FROM users WHERE id = ?";
    private static final String AUTHENTICATE_USER = "SELECT * FROM users WHERE username = ?";
    private static final String LIST_USERS = "SELECT * FROM users";
    private static final String UPDATE_USER = "UPDATE users SET username = ?, password = ? WHERE id = ?";
    private static final String DELETE_USER = "DELETE FROM users WHERE id = ?";

    // Create Methode zum Erstellen eines Users in der Datenbank
    public int createUser(User user) throws SQLException {
        try (Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(CREATE_USER)) {
            buildUserParams(stmt, user);
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    // Verschiedene Read Methoden:
    // Suche nach User anhand der id in der DB
    public User getUser(int id) throws SQLException {
        User user = new User();
        try (Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(GET_USER_BY_ID)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    user.setId(id);
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                }
            }
        }
        return user;
    }

    // Methode zum Vergleichen des eingegebenen Passworts mit dem in der Datenbank
    public boolean authenticateUser(String username, String password) throws SQLException {
        try (Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(AUTHENTICATE_USER)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Mit BCrypt das Passwort vergleichen
                    return BCrypt.checkpw(password, rs.getString("password"));
                }
            }
        }
        return false;
    }

    // Auflistung aller Meals
    public List<User> listUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        try (Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(LIST_USERS)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt(1));
                    user.setUsername(rs.getString(2));
                    user.setPassword(rs.getString(3));
                    users.add(user);
                }
            }
        }
        return users;
    }

    // Update Methode, wenn man einen User in der DB verändern möchte
    public int updateUser(User user) throws SQLException {
        try (Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(UPDATE_USER)) {
            buildUserParams(stmt, user);
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    // Delete Methode, zum Löschen eines Users aus der DB
    public int deleteUser(int id) throws SQLException {
        try (Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(DELETE_USER)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }


    // Helper Methoden, nur zum Organisieren des Codes, damit obige Methoden nicht zu gross sind
    // baut ein Statement mit einem ganzen User Objekt auf
    private void buildUserParams(PreparedStatement stmt, User user) throws SQLException {
        stmt.setString(1, user.getUsername());
        // Hier wird BCrypt verwendet um das Passwort zu hashen
        stmt.setString(2, BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
    }
}
