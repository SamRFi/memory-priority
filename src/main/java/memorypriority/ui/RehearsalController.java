package memorypriority.ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
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
    private TextArea valueTextArea;

    @FXML
    private VBox overviewVBox;

    @FXML
    private Button rehearseAgainButton;

    @FXML
    private Button returnToDashboardButton;

    @FXML
    private Label overviewLabel;

    @FXML
    private Button saveChangesButton;

    @FXML
    private Button resetButton;

    private boolean inRandomOrder = false;
    private boolean keyToValue = true;
    private MemorySet memorySet;
    private MemorySetService memorySetService;

    private DashboardController dashboardController;

    private Dialog<Void> dialog;

    private Map.Entry<String, String> currentPair;

    private final List<Map.Entry<String, String>> rehearsedPairs = new ArrayList<>();

    private String originalKey;
    private String originalValue;

    public void setDashboardController(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    public void setDialog(Dialog<Void> dialog) {
        this.dialog = dialog;
    }

    public void setMemorySet(MemorySet memorySet) {
        this.memorySet = memorySet;
    }

    public void setMemorySetService(MemorySetService memorySetService) {
        this.memorySetService = memorySetService;
    }

    @FXML
    private void initialize() {
        saveChangesButton.setVisible(false);
        resetButton.setVisible(false);
        valueTextArea.setEditable(true);

        keyLabel.textProperty().addListener((observable, oldValue, newValue) -> {
            if (originalKey != null && !newValue.equals(originalKey)) {
                showEditButtons();
            }
        });

        valueTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
            if (originalValue != null && !newValue.equals(originalValue)) {
                showEditButtons();
            }
        });
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
        showValueButton.setText(keyToValue ? "Show Value" : "Show Key");

        if (valueTextArea.isVisible()) {
            String newValue = keyToValue ? currentPair.getValue() : currentPair.getKey();
            valueTextArea.setText(newValue);
            originalValue = newValue;
        } else {
            originalValue = null;
        }

        updateKeyLabel();

        // Reset edit state
        hideEditButtons();

        // Update button labels
        saveChangesButton.setText(keyToValue ? "Save Changes" : "Save Key Change");
        resetButton.setText(keyToValue ? "Reset" : "Reset Key");
    }

    private void updateKeyLabel() {
        if (currentPair != null) {
            keyLabel.setText(keyToValue ? currentPair.getKey() : currentPair.getValue());
            originalKey = keyLabel.getText();
        }
    }

    @FXML
    private void showValue() {
        valueTextArea.setVisible(true);
        valueTextArea.setText(keyToValue ? currentPair.getValue() : currentPair.getKey());
        originalValue = valueTextArea.getText();
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
        updateKeyLabel();
        valueTextArea.clear();
        valueTextArea.setVisible(false);
        originalValue = null;
        hideEditButtons();
    }

    @FXML
    private void startRehearsal() {
        resetRehearsalState();
        if (inRandomOrder) {
            memorySet.shuffle();
        }
        currentPair = memorySet.getFirstPair();
        updateKeyLabel();

        orderButton.setDisable(true);
        modeButton.setDisable(true);
        showValueButton.setDisable(false);
        nextButton.setDisable(false);

        startButton.setVisible(false);
        valueTextArea.setVisible(false);

        rehearsedPairs.clear();
        nextPair();
        orderButton.setDisable(true);
        modeButton.setDisable(false);
    }

    private void showEditButtons() {
        saveChangesButton.setVisible(true);
        resetButton.setVisible(true);
    }

    private void hideEditButtons() {
        saveChangesButton.setVisible(false);
        resetButton.setVisible(false);
    }

    @FXML
    private void saveChanges() {
        String newKey = keyToValue ? keyLabel.getText() : valueTextArea.getText();
        String newValue = keyToValue ? valueTextArea.getText() : keyLabel.getText();

        memorySetService.updateKeyValuePair(memorySet, originalKey, originalValue, newKey, newValue);

        currentPair = Map.entry(newKey, newValue);
        originalKey = newKey;
        originalValue = newValue;

        hideEditButtons();
    }

    @FXML
    private void resetChanges() {
        keyLabel.setText(originalKey);
        valueTextArea.setText(originalValue);
        hideEditButtons();
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
        overviewScrollPane.setVisible(false);
        resetRehearsalState();
    }

    @FXML
    private void returnToDashboard() {
        memorySetService.rehearseMemorySet(memorySet);
        dashboardController.refreshUI();
        if (dialog != null) {
            dialog.close();
        }
    }

    private void resetRehearsalState() {
        orderButton.setDisable(false);
        modeButton.setDisable(false);
        startButton.setVisible(true);
        showValueButton.setDisable(true);
        nextButton.setDisable(true);

        keyLabel.setText("");
        valueTextArea.setText("");
        valueTextArea.setVisible(false);

        hideEditButtons();

        if(memorySet != null) {
            memorySet.resetCurrentIndex();
        }

        originalKey = null;
        originalValue = null;
    }
}