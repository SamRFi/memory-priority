package memorypriority.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import memorypriority.service.AuthenticationService;
import memorypriority.domain.User;
import memorypriority.util.MemoryPriorityException;

import java.io.IOException;
import java.util.Objects;

public class LoginScreenController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button cancelButton;

    @FXML
    private Button loginButton;

    private AuthenticationService authService;

    public LoginScreenController() {
        this.authService = new AuthenticationService();
    }

    @FXML
    public void initialize() {
        cancelButton.setOnAction(event -> System.exit(0));
        loginButton.setOnAction(event -> login());
    }

    private void login() {
        try {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();
            User user = authService.login(username, password);

            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Login successful");
            alert.setHeaderText(null);
            alert.setContentText(user.getUsername() + "has logged in successfully!");
            alert.showAndWait();

            Stage stage = (Stage) loginButton.getScene().getWindow();
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/dashboard.fxml")));
            Scene scene = new Scene(root);
            stage.setScene(scene);

            // If login is successful, you can transition to another screen here.
            // You will also have access to the `user` object for use in your application.
        } catch (MemoryPriorityException ex) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Login Failed");
            alert.setHeaderText(null);
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        } catch (IOException e) {
            throw new MemoryPriorityException("Failed to open new fxml window", e);
        }
    }
}
