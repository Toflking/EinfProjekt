
/*
Datum: 18.3.2026
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


// Aus unserem Projekt, für Überprüfen der Verbindung
import db.DbInit;
// Application von JavaFX um unsere App zu starten
import javafx.application.Application;
// Für Exceptions
import java.sql.SQLException;

public class Main {
    // main Methode, hier beginnt das ganze
    public static void main(String[] args) throws SQLException {
            // Überprüfen der DB connection
            System.out.println("Checking database connection...");
            if (DbInit.checkConnection()) {
                // JavaFX launchen
                Application.launch(App.class, args);
            } else {
                System.out.println("Database connection failed, UI will not start");
            }

    }
}