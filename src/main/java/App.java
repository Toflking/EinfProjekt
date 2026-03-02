import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

// Launcht die App
public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // fxml File laden aus dem resources ordner
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/main_view.fxml")));

        // Scene erstellen mit JavaFx
        Scene scene = new Scene(root, 600, 800);
        scene.getStylesheets().add(
                getClass().getResource("/css/style.css").toExternalForm()
        );
        stage.setTitle("Meal Planner System");
        stage.setScene(scene);
        stage.show();
    }
}