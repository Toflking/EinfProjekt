package view;

import dao.UserDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.User;

import java.io.IOException;
import java.sql.SQLException;

// Diese Klasse ist zuständig für alles was im Registrierungsscreen vor sich geht
public class RegisterController {

    // Felder aus dem FXML
    @FXML private AnchorPane root;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label registerFailedLabel;

    // UserDAO Objekt für Backend Zugriff
    private final UserDAO userDAO = new UserDAO();

    // Registrieren, sobald auf Registrierungsbutton geklickt wird
    @FXML
    public void register() throws SQLException, IOException {
        // Username und Passwort aus Textfeldern holen
        String username =  usernameField.getText();
        String password = passwordField.getText();

        // Sichergehen, dass Felder nicht leer sind
        if (username.isBlank() || password.isBlank()) {
            registerFailedLabel.setText("Please enter your username and password");
            registerFailedLabel.setVisible(true);
            return;
        }

        // Neuen User damit erstellen
        User user = new User(username, password);

        // Überprüfen, dass kein anderer User mit diesem Namen existiert
        if (userDAO.getUserByUsername(username) != null) {
            registerFailedLabel.setText("This username is already in use");
            registerFailedLabel.setVisible(true);
            return;
        }

        // Erstellt User
        // Create User gibt ID des Users zurück, wenn dieser erstellt wird, daher >0, da ID != 0 sein kann
        if (userDAO.createUser(user) > 0) {
            // User Abfragen
            user = userDAO.getUserByUsername(username);
            // FXML laden
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main_view.fxml"));
            Parent root = loader.load();
            // Controller setzen
            MainController controller = loader.getController();
            // User an den Controller weitergeben
            controller.setCurrentUser(user);

            // Scene setzen
            Stage stage = (Stage) this.root.getScene().getWindow();
            stage.getScene().setRoot(root);
        } else {
            registerFailedLabel.setText("User Creation Failed");
            registerFailedLabel.setVisible(true);
        }
    }

    // Methode bringt den User zurück zum Login Screen
    @FXML
    public void back() throws IOException {
        // FXML laden
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Parent root = loader.load();

        // Scene setzen
        Stage stage = (Stage) this.root.getScene().getWindow();
        stage.getScene().setRoot(root);
    }
}
