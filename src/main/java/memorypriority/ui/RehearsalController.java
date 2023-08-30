package memorypriority.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import memorypriority.domain.MemorySet;
import memorypriority.service.MemorySetService;

public class RehearsalController {

    @FXML
    private Button orderButton;

    @FXML
    private Button modeButton;

    @FXML
    private Button showValueButton;

    @FXML
    private Button nextButton;

    @FXML
    private Button startButton;

    @FXML
    private Label keyLabel;

    @FXML
    private Label valueLabel;

    private boolean inRandomOrder = false; // Track order preference
    private boolean keyToValue = true;    // Track mode preference
    private MemorySet memorySet;
    private MemorySetService memorySetService;

    public void setMemorySet(MemorySet memorySet) {
        this.memorySet = memorySet;
    }

    public void setMemorySetService(MemorySetService memorySetService) {
        this.memorySetService = memorySetService;
    }

    // Add logic for toggling order and mode preferences
    @FXML
    private void toggleOrder() {
        // Toggle the inRandomOrder flag
    }

    @FXML
    private void toggleMode() {
        // Toggle the keyToValue flag
    }

    // Add logic to handle "Show Value" button click
    @FXML
    private void showValue() {
        // Show the valueLabel and update its text
    }

    // Add logic to handle "Next" button click
    @FXML
    private void nextPair() {
        // Show the next key-value pair
    }

    // Add logic to handle "Start" button click
    @FXML
    private void startRehearsal() {
        // Initialize and start the rehearsal process
    }
}

