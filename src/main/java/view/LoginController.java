package view;

import dao.UserDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.User;

import java.io.IOException;
import java.sql.SQLException;

// Diese Klasse kontrolliert alles, was im Login Screen vor sich geht
public class LoginController {

    // Felder aus dem FXML setzen
    @FXML private AnchorPane root;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label loginFailedLabel;

    // UserDAO erstellen für Zugriff zum Backend
    private final UserDAO userDAO = new UserDAO();

    // Methode für Login
    @FXML
    public void login() {
        // Sicherstellen, dass das Error Label weg ist
        loginFailedLabel.setVisible(false);
        // Username und Passwort holen
        String username = usernameField.getText();
        String password = passwordField.getText();

        try {
            // Überprüfen ob Username oder Passwort leergelassen wurden
            if (username.isBlank() || password.isBlank()) {
                loginFailedLabel.setText("Please enter your username and password");
                loginFailedLabel.setVisible(true);
                return;
            }
            // User authentifizieren, gibt 1 zurück bei korrekten Angaben
            if (userDAO.authenticateUser(username, password)) {
                // Da id noch nicht gesetzt wurde, wird hier auch noch die id des Users abgefragt und gesetzt
                User user = userDAO.getUserByUsername(username);
                // FXML laden
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main_view.fxml"));
                Parent root = loader.load();

                // Controller erstellen
                MainController controller = loader.getController();
                // User an den Controller übergeben
                controller.setCurrentUser(user);
                // Scene setzen
                Stage stage = (Stage) this.root.getScene().getWindow();
                stage.getScene().setRoot(root);

            } else {
                loginFailedLabel.setText("Username or password incorrect");
                loginFailedLabel.setVisible(true);
            }
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Für Registrierungsbutton, ruft den Registrierungsscreen auf
    @FXML
    public void register() throws IOException {
        // FXML file laden
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/register.fxml"));
        Parent root = loader.load();

        // Scene setzen
        Stage stage = (Stage) this.root.getScene().getWindow();
        stage.getScene().setRoot(root);
    }
}
