import db.DbInit;
import javafx.application.Application;
import java.sql.SQLException;


/*
Datum: 20.2.2026
Projektname: Applikation für Rezeptsuche
Namen: Tobias Flammer G22L, Luis Ritzmann G22B
Hauptquellen:
- https://www.themealdb.com/
- https://docs.oracle.com/en/java/javase/20/docs/api/java.base/module-summary.html
- https://www.java-forum.org/
- https://www.youtube.com/watch?v=VUVqamT8Npc
Grobe Aufteilung des Codes:
Tobias: Backend, Datenbank setup (Website Parsen) und Teile des Frontends
Luis: Teile vom Frontend




 */


public class Main {
    // main Methode, hier beginnt das ganze
    public static void main(String[] args) {
        try {
            // Überprüfen der DB connection
            System.out.println("Checking database connection...");
            DbInit.checkConnection();

            // JavaFX launchen
            Application.launch(App.class, args);

        } catch (SQLException e) {
            System.err.println("Could not connect to the database. UI will not start.");
            e.printStackTrace();
        }
    }
}