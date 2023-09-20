package memorypriority.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class FxApplication extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        loader.setResources(ResourceBundle.getBundle("internationalization/text", Locale.forLanguageTag("en")));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}

