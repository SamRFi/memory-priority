package memorypriority.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import memorypriority.domain.MemorySet;
import memorypriority.service.MemorySetService;

import java.util.Map;

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

    // Add a field to store the current key-value pair
    private Map.Entry<String, String> currentPair;

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
        inRandomOrder = !inRandomOrder; // Flip the boolean value
        orderButton.setText(inRandomOrder ? "Random Order" : "Sequential Order"); // Update the button text accordingly
    }

    @FXML
    private void toggleMode() {
        // Toggle the keyToValue flag
        keyToValue = !keyToValue; // Flip the boolean value
        modeButton.setText(keyToValue ? "Key to Value" : "Value to Key"); // Update the button text accordingly
        valueLabel.setVisible(false); // Hide the value label when switching modes
    }

    // Add logic to handle "Show Value" button click
    @FXML
    private void showValue() {
        // Show the valueLabel and update its text
        valueLabel.setVisible(true); // Make the value label visible
        valueLabel.setText(keyToValue ? currentPair.getValue() : currentPair.getKey()); // Set the text to the corresponding value or key of the current pair
    }

    // Add logic to handle "Next" button click
    @FXML
    private void nextPair() {
        // Show the next key-value pair
        if (inRandomOrder) {
            currentPair = memorySet.getRandomPair(); // Get a random pair from the memory set
        } else {
            currentPair = memorySet.getNextPair(); // Get the next pair in sequence from the memory set
        }
        keyLabel.setText(keyToValue ? currentPair.getKey() : currentPair.getValue()); // Set the key label to the corresponding key or value of the current pair
        valueLabel.setVisible(false); // Hide the value label until showValue is clicked
    }

    // Add logic to handle "Start" button click
    @FXML
    private void startRehearsal() {
        // Initialize and start the rehearsal process
        memorySet.shuffle(); // Shuffle the memory set to randomize the order of pairs
        currentPair = memorySet.getFirstPair(); // Get the first pair from the memory set
        keyLabel.setText(keyToValue ? currentPair.getKey() : currentPair.getValue()); // Set the key label to the corresponding key or value of the current pair

        // Enable and disable buttons as needed
        orderButton.setDisable(true); // Disable the order button once rehearsal starts
        modeButton.setDisable(true);  // Disable the mode button once rehearsal starts
        showValueButton.setDisable(false); // Enable the show value button once rehearsal starts
        nextButton.setDisable(false);  // Enable the next button once rehearsal starts

        startButton.setVisible(false);  // Hide the start button once rehearsal starts

        valueLabel.setVisible(false);  // Hide the value label until showValue is clicked


    }

}

