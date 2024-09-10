package memorypriority.ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import memorypriority.domain.MemorySet;
import memorypriority.service.MemorySetService;

import java.util.ArrayList;
import java.util.Collections;
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
    private TextArea keyTextArea;

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
    private boolean isChangingOrder = false;
    private boolean keyToValue = true;
    private MemorySet memorySet;
    private MemorySetService memorySetService;

    private DashboardController dashboardController;

    private Dialog<Void> dialog;

    private Map.Entry<String, String> currentPair;

    private final List<Map.Entry<String, String>> rehearsedPairs = new ArrayList<>();

    private List<Map.Entry<String, String>> currentRehearsalOrder;
    private int currentPairIndex;

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
        keyTextArea.setEditable(true);

        keyTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!isChangingOrder && originalKey != null && !newValue.equals(originalKey)) {
                showEditButtons();
            }
        });

        valueTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!isChangingOrder && originalValue != null && !newValue.equals(originalValue)) {
                showEditButtons();
            }
        });
    }

    @FXML
    private void toggleOrder() {
        isChangingOrder = true;
        inRandomOrder = !inRandomOrder;
        orderButton.setText(inRandomOrder ? "Random Order" : "Sequential Order");
        resetRehearsalOrder();
        isChangingOrder = false;
    }

    private void resetRehearsalOrder() {
        currentRehearsalOrder = new ArrayList<>(memorySet.getPairList());
        if (inRandomOrder) {
            Collections.shuffle(currentRehearsalOrder);
        }
        if (!rehearsedPairs.isEmpty()) {
            currentPairIndex = Math.min(currentPairIndex, currentRehearsalOrder.size() - 1);
            currentPair = currentRehearsalOrder.get(currentPairIndex);
            updateKeyTextAreaSilently();
            updateValueAreaSilently();
        }
    }

    private void updateKeyTextAreaSilently() {
        isChangingOrder = true;
        updateKeyTextArea();
        isChangingOrder = false;
    }

    private void updateValueAreaSilently() {
        isChangingOrder = true;
        if (valueTextArea.isVisible()) {
            valueTextArea.setText(keyToValue ? currentPair.getValue() : currentPair.getKey());
        }
        isChangingOrder = false;
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

        updateKeyTextArea();

        hideEditButtons();

        saveChangesButton.setText(keyToValue ? "Save Changes" : "Save Key Change");
        resetButton.setText(keyToValue ? "Reset" : "Reset Key");
    }

    private void updateKeyTextArea() {
        if (currentPair != null) {
            keyTextArea.setText(keyToValue ? currentPair.getKey() : currentPair.getValue());
            originalKey = keyTextArea.getText();
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
        if (rehearsedPairs.size() == currentRehearsalOrder.size()) {
            showOverview();
            return;
        }

        currentPairIndex = rehearsedPairs.size();
        currentPair = currentRehearsalOrder.get(currentPairIndex);
        rehearsedPairs.add(currentPair);
        updateKeyTextArea();
        valueTextArea.clear();
        valueTextArea.setVisible(false);
        originalValue = null;
        hideEditButtons();
    }

    @FXML
    private void startRehearsal() {
        resetRehearsalState();
        resetRehearsalOrder();
        currentPair = currentRehearsalOrder.get(currentPairIndex);
        updateKeyTextArea();

        orderButton.setDisable(false);
        modeButton.setDisable(true);
        showValueButton.setDisable(false);
        nextButton.setDisable(false);

        startButton.setVisible(false);
        valueTextArea.setVisible(false);

        rehearsedPairs.clear();
        nextPair();
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
        String newKey, newValue;
        if (keyToValue) {
            newKey = keyTextArea.getText();
            newValue = valueTextArea.isVisible() ? valueTextArea.getText() : currentPair.getValue();
        } else {
            newKey = valueTextArea.isVisible() ? valueTextArea.getText() : currentPair.getKey();
            newValue = keyTextArea.getText();
        }

        memorySetService.updateKeyValuePair(memorySet, currentPair.getKey(), currentPair.getValue(), newKey, newValue);

        currentPair = Map.entry(newKey, newValue);
        currentRehearsalOrder.set(currentPairIndex, currentPair);

        originalKey = newKey;
        originalValue = newValue;

        keyTextArea.setText(keyToValue ? newKey : newValue);
        if (valueTextArea.isVisible()) {
            valueTextArea.setText(keyToValue ? newValue : newKey);
        }

        hideEditButtons();
    }

    @FXML
    private void resetChanges() {
        keyTextArea.setText(originalKey);
        valueTextArea.setText(originalValue);
        hideEditButtons();
    }

    private void showOverview() {
        overviewScrollPane.setVisible(true);
        nextButton.setDisable(true);
        showValueButton.setDisable(true);

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> pair : currentRehearsalOrder) {
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
        startRehearsal();
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

        keyTextArea.setText("");
        valueTextArea.setText("");
        valueTextArea.setVisible(false);

        hideEditButtons();

        if(memorySet != null) {
            memorySet.resetCurrentIndex();
        }

        originalKey = null;
        originalValue = null;
        currentPairIndex = 0;
        rehearsedPairs.clear();
    }
}