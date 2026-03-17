package dao;

import db.Database;
import model.User;
// Zum hashen der Passwörter
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
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
    private static final String CREATE_USER = "INSERT INTO users (username, password_hash) VALUES (?, ?)";
    private static final String GET_USER_BY_ID = "SELECT * FROM users WHERE id = ?";
    private static final String GET_USER_BY_NAME = "SELECT * FROM users WHERE username = ?";
    private static final String LIST_USERS = "SELECT * FROM users";
    private static final String UPDATE_USER = "UPDATE users SET username = ?, password_hash = ? WHERE id = ?";
    private static final String DELETE_USER = "DELETE FROM users WHERE id = ?";

    // Create Methode zum Erstellen eines Users in der Datenbank
    public int createUser(User user) throws SQLException {
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(CREATE_USER, Statement.RETURN_GENERATED_KEYS)) {
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
                    user.setPassword(rs.getString("password_hash"));
                }
            }
        }
        return user;
    }

    // Suche nach User anhand Username, wenn kein User gefunden wird, wird null returned
    public User getUserByUsername(String username) throws SQLException {
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_USER_BY_NAME)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password_hash"));
                    return user;
                }
            }
        }
        return null;
    }

    // Methode zum Vergleichen des eingegebenen Passworts mit dem in der Datenbank
    public boolean authenticateUser(String username, String password) throws SQLException {
        try (Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(GET_USER_BY_NAME)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Mit BCrypt das Passwort vergleichen
                    return BCrypt.checkpw(password, rs.getString("password_hash"));
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
            stmt.setString(1, user.getUsername());
            stmt.setString(2, resolvePasswordHash(user));
            stmt.setInt(3, user.getId());
            return stmt.executeUpdate();
        }
    }

    // Delete Methode, zum Löschen eines Users aus der DB
    public int deleteUser(int id) throws SQLException {
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_USER)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate();
        }
    }


    // Helper Methoden, nur zum Organisieren des Codes, damit obige Methoden nicht zu gross sind
    // baut ein Statement mit einem ganzen User Objekt auf
    private void buildUserParams(PreparedStatement stmt, User user) throws SQLException {
        stmt.setString(1, user.getUsername());
        // Hier wird BCrypt verwendet um das Passwort zu hashen
        stmt.setString(2, BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
    }

    // Helper Methode zum Umwandeln des Passworts in einen Hash mithilfe von BCrypt
    private String resolvePasswordHash(User user) throws SQLException {
        String password = user.getPassword();

        if (password == null || password.isBlank()) {
            return getPasswordHashByUserId(user.getId());
        }

        // Wenn Passwort bereits ein Hash ist, dann nicht nochmal Hashen
        // Dies würde sonst vorallem beim Updaten des Passworts Probleme bereiten
        if (password.startsWith("$2a$") || password.startsWith("$2b$") || password.startsWith("$2y$")) {
            return password;
        }

        // Hashen
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    // Methode zum Bekommen des Passworts nach Id, wird gebraucht für das korrekte Hashen
    private String getPasswordHashByUserId(int userId) throws SQLException {
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_USER_BY_ID)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("password_hash");
                }
            }
        }

        throw new SQLException("User not found for password update");
    }
}
