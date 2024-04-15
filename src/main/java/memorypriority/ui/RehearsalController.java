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

    private boolean inRandomOrder = false;
    private boolean keyToValue = true;
    private MemorySet memorySet;
    private MemorySetService memorySetService;

    private DashboardController dashboardController;

    public void setDashboardController(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    private Dialog<Void> dialog;

    public void setDialog(Dialog<Void> dialog) {
        this.dialog = dialog;
    }

    private Map.Entry<String, String> currentPair;

    private final List<Map.Entry<String, String>> rehearsedPairs = new ArrayList<>();

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
        showValueButton.setText(keyToValue ? "Show Value" : "Show Key"); // Update the show button text

        // Update the revealed content if the value/key is currently visible
        if (valueTextArea.isVisible()) {
            valueTextArea.setText(keyToValue ? currentPair.getValue() : currentPair.getKey());
        }

        // Update the key label to show the appropriate key or value based on the new mode
        keyLabel.setText(keyToValue ? currentPair.getKey() : currentPair.getValue());
    }

    @FXML
    private void showValue() {
        valueTextArea.setVisible(true);
        valueTextArea.setText(keyToValue ? currentPair.getValue() : currentPair.getKey());
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
        valueTextArea.setVisible(false);
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
        valueTextArea.setVisible(false);

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
        // Reset visibility and enablement of buttons
        orderButton.setDisable(false);
        modeButton.setDisable(false);
        startButton.setVisible(true);
        showValueButton.setDisable(true);
        nextButton.setDisable(true);

        // Reset labels
        keyLabel.setText("");
        valueTextArea.setText("");
        valueTextArea.setVisible(false);

        // Reset the current memory set index (assuming the MemorySet class has such a method)
        if(memorySet != null) {
            memorySet.resetCurrentIndex();
        }
    }


}
