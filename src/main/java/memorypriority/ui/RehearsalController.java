package memorypriority.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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

    private boolean inRandomOrder = false;
    private boolean keyToValue = true;
    private MemorySet memorySet;
    private MemorySetService memorySetService;

    private Map.Entry<String, String> currentPair;

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
        keyToValue = !keyToValue;
        modeButton.setText(keyToValue ? "Key to Value" : "Value to Key");
        valueLabel.setVisible(false);
    }

    @FXML
    private void showValue() {
        valueLabel.setVisible(true);
        valueLabel.setText(keyToValue ? currentPair.getValue() : currentPair.getKey());
    }

    @FXML
    private void nextPair() {
        currentPair = memorySet.getNextPair();
        keyLabel.setText(keyToValue ? currentPair.getKey() : currentPair.getValue());
        valueLabel.setVisible(false);
    }

    @FXML
    private void startRehearsal() {
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
    }
}
