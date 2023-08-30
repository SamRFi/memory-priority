package memorypriority.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import memorypriority.domain.MemorySet;
import memorypriority.service.MemorySetService;

import java.util.ArrayList;
import java.util.List;
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
    private ScrollPane overviewScrollPane;

    @FXML
    private Button startButton;

    @FXML
    private Label keyLabel;

    @FXML
    private Label valueLabel;

    @FXML
    private VBox overviewVBox;

    @FXML
    private Button rehearseAgainButton;

    @FXML
    private Button returnToDashboardButton;

    @FXML
    private Label overviewLabel;

    private boolean inRandomOrder = false;
    private boolean keyToValue = true;
    private MemorySet memorySet;
    private MemorySetService memorySetService;

    private Map.Entry<String, String> currentPair;

    private List<Map.Entry<String, String>> rehearsedPairs = new ArrayList<>();

    public void setMemorySet(MemorySet memorySet) {
        this.memorySet = memorySet;
    }

    public void setMemorySetService(MemorySetService memorySetService) {
        this.memorySetService = memorySetService;
    }

    @FXML
    private void toggleOrder() {
        inRandomOrder = !inRandomOrder;
        orderButton.setText(inRandomOrder ? "Random Order" : "Sequential Order");
        if (inRandomOrder) {
            memorySet.shuffle();
        }
    }

    @FXML
    private void toggleMode() {
        // Toggle the keyToValue flag
        keyToValue = !keyToValue; // Flip the boolean value
        modeButton.setText(keyToValue ? "Key to Value" : "Value to Key"); // Update the button text accordingly
        valueLabel.setVisible(false); // Hide the value label when switching modes
    }



    @FXML
    private void showValue() {
        valueLabel.setVisible(true);
        valueLabel.setText(keyToValue ? currentPair.getValue() : currentPair.getKey());
    }

    @FXML
    private void nextPair() {
        if (memorySet.getPairList().size() == rehearsedPairs.size()) {
            showOverview();
            return;
        }

        currentPair = memorySet.getNextPair();

        while (rehearsedPairs.contains(currentPair)) {
            currentPair = memorySet.getNextPair();
        }

        rehearsedPairs.add(currentPair);
        keyLabel.setText(keyToValue ? currentPair.getKey() : currentPair.getValue());
        valueLabel.setVisible(false);
    }

    @FXML
    private void startRehearsal() {
        resetRehearsalState();
        if (inRandomOrder) {
            memorySet.shuffle();
        }
        currentPair = memorySet.getFirstPair();
        keyLabel.setText(keyToValue ? currentPair.getKey() : currentPair.getValue());

        orderButton.setDisable(true);
        modeButton.setDisable(true);
        showValueButton.setDisable(false);
        nextButton.setDisable(false);

        startButton.setVisible(false);
        valueLabel.setVisible(false);

        rehearsedPairs.clear();
        nextPair();
        orderButton.setDisable(true);
        modeButton.setDisable(false);
    }

    private void showOverview() {
        overviewScrollPane.setVisible(true);
        nextButton.setDisable(true);
        showValueButton.setDisable(true);

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> pair : rehearsedPairs) {
            sb.append(pair.getKey()).append(" : ").append(pair.getValue()).append("\n");
        }
        overviewLabel.setText(sb.toString());

        rehearseAgainButton.setVisible(true);
        returnToDashboardButton.setVisible(true);
    }

    @FXML
    private void rehearseAgain() {
        resetRehearsalState();
    }

    @FXML
    private void returnToDashboard() {
        // Implement logic to return to dashboard
    }

    private void resetRehearsalState() {
        // Reset visibility and enablement of buttons
        orderButton.setDisable(false);
        modeButton.setDisable(false);
        startButton.setVisible(true);
        showValueButton.setDisable(true);
        nextButton.setDisable(true);

        // Reset labels
        keyLabel.setText("");
        valueLabel.setText("");
        valueLabel.setVisible(false);

        // Reset the current memory set index (assuming the MemorySet class has such a method)
        if(memorySet != null) {
            memorySet.resetCurrentIndex();
        }
    }


}
