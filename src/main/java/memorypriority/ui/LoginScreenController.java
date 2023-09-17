package memorypriority.ui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import memorypriority.service.AuthenticationService;
import memorypriority.service.MemorySetService;
import memorypriority.util.MemoryPriorityException;

import java.io.IOException;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

public class LoginScreenController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Button addProfileButton;

    @FXML
    private Button removeProfileButton;

    @FXML
    private ComboBox<String> profileComboBox;

    private AuthenticationService authService;

    public LoginScreenController() {
        this.authService = new AuthenticationService();
    }

    @FXML
    public void initialize() {
        loginButton.setOnAction(event -> login());
        addProfileButton.setOnAction(event -> addProfile());
        removeProfileButton.setOnAction(event -> removeProfile());
        profileComboBox.setItems(FXCollections.observableArrayList(authService.getAllUsernames()));
    }

    private void login() {
        try {
            String username = profileComboBox.getValue().trim();
            authService.login(username);

            Stage stage = (Stage) loginButton.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard.fxml"));

            loader.setResources(ResourceBundle.getBundle("internationalization/text", Locale.forLanguageTag("en")));
            Parent root = loader.load();

            DashboardController dashboardController = loader.getController();
            dashboardController.setMemorySetService(new MemorySetService(username));
            dashboardController.populateMemorySets();

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

    private void addProfile() {
        String usernameToAdd = usernameField.getText().trim();
        authService.addProfile(usernameToAdd);
        profileComboBox.setItems(FXCollections.observableArrayList(authService.getAllUsernames()));
    }

    private void removeProfile() {
        String usernameToRemove = profileComboBox.getValue();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Remove Profile");
        alert.setHeaderText("Are you sure you want to remove this profile?");
        alert.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            authService.removeProfile(usernameToRemove);
            profileComboBox.setItems(FXCollections.observableArrayList(authService.getAllUsernames()));
        }
    }

}
