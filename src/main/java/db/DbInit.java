package db;

import java.sql.Connection;
import java.sql.SQLException;
// Für die Initialisierung der DB
public class DbInit {
    // Überprüft Connection und gibt entsprechenden Konsolen output und Rückgabewert
    public static boolean checkConnection() throws SQLException {
        try (Connection conn = Database.getConnection()) {
            System.out.println("DB connection OK");
            return true;
        } catch (SQLException ex) {
            System.out.println("DB connection ERROR");
            System.out.println(ex.getMessage());
            return false;
        }

    }
}
