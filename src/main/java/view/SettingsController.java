package view;

import dao.UserDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.User;

import java.io.IOException;
import java.sql.SQLException;

// Diese Klasse ist zuständig für alles, was im Settingsscreen passiert
public class SettingsController {

    // FXML Felder
    @FXML private Label usernameLabel;
    @FXML private AnchorPane root;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label settingsFailedLabel;

    // UserDAO Objekt für den Zugriff auf das Backend
    private final UserDAO userDAO = new UserDAO();

    // User, wird mitgegeben durch MainController
    private User currentUser;

    // Methode um den Account zu Updaten
    @FXML
    private void updateAccount() throws SQLException {
        // Zurücksetzen des FailedLabels
        settingsFailedLabel.setVisible(false);
        // Username und Passwort holen
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Falls beide Leer
        if (username.isBlank() && password.isBlank()) {
            settingsFailedLabel.setText("Please enter a new username or password");
            settingsFailedLabel.setVisible(true);
            settingsFailedLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        // Falls Username nicht leer gelassen
        if (!username.isBlank()) {
            // User zum username holen
            // == null falls nicht existiert
            User existingUser = userDAO.getUserByUsername(username);
            // Überprüfen, dass Unique Username ist
            if (existingUser != null && existingUser.getId() != currentUser.getId()) {
                settingsFailedLabel.setText("This username is already in use");
                settingsFailedLabel.setVisible(true);
                settingsFailedLabel.setStyle("-fx-text-fill: red;");
                return;
            }
            // Username setzen
            currentUser.setUsername(username);
        }

        // Wenn Passwort nicht leer gelassen
        if (!password.isBlank()) {
            // Passwort setzen
            currentUser.setPassword(password);
        }

        // User Updaten
        // Gibt 1 bei Erfolg zurück
        if (userDAO.updateUser(currentUser) > 0) {
            // User zur Sicherheit nochmal aus der DB holen
            currentUser = userDAO.getUserByUsername(username);
            settingsFailedLabel.setText("User Update Successful");
            settingsFailedLabel.setVisible(true);
            settingsFailedLabel.setStyle("-fx-text-fill: green;");
        } else {
            settingsFailedLabel.setText("User Update Failed");
            settingsFailedLabel.setVisible(true);
            settingsFailedLabel.setStyle("-fx-text-fill: red;");
        }
    }

    // Methode zum Löschen des Accounts
    @FXML
    private void deleteAccount() throws IOException, SQLException {
        // Resetten des FailedLabels
        settingsFailedLabel.setVisible(false);
        // Löschen des Users
        // Gibt 1 zurück, wenn erfolgreich
        if (userDAO.deleteUser(currentUser.getId()) > 0) {
            // FXML laden
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();

            // Scene setzen
            Stage stage = (Stage) this.root.getScene().getWindow();
            stage.getScene().setRoot(root);
        } else  {
            settingsFailedLabel.setText("User Delete Failed");
            settingsFailedLabel.setVisible(true);
            settingsFailedLabel.setStyle("-fx-text-fill: red;");
        }
    }

    // Methode zum Ausloggen des Users
    @FXML
    private void logout() throws IOException {
        this.currentUser = null;

        // FXML laden
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Parent root = loader.load();

        // Scene setzen
        Stage stage = (Stage) this.root.getScene().getWindow();
        stage.getScene().setRoot(root);
    }

    // Zurück Button um zurück zur Main View zu kommen
    @FXML
    private void goBack() throws IOException {
        // FXML laden
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main_view.fxml"));
        Parent root = loader.load();

        // User mitgeben, da evtl geändert
        MainController controller = loader.getController();
        controller.setCurrentUser(currentUser);
        this.currentUser = null;

        // Scene setzen
        Stage stage = (Stage) this.root.getScene().getWindow();
        stage.getScene().setRoot(root);
    }


    // Damit der MainController den User mitgeben kann
    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
        usernameLabel.setText(currentUser.getUsername());
    }
}
