package memorypriority.ui;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import memorypriority.domain.MemorySet;
import memorypriority.domain.PriorityLevel;
import memorypriority.service.MemorySetService;

import java.util.HashMap;
import java.util.Map;

public class AddMemorySetController {


    private MemorySetService memorySetService;
    private DashboardController dashboardController;

    private Map<String, String> keyValueCache = new HashMap<>();


    public void setMemorySetService(MemorySetService memorySetService) {
        this.memorySetService = memorySetService;
    }

    public void setDashboardController(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    @FXML
    public Button saveButton;

    @FXML
    private GridPane keyValuePairsContainer;

    @FXML
    private TextField memorySetName;

    @FXML
    private ComboBox<String> priorityLevel;

    @FXML
    public void initialize() {
        handleAddMoreRowsButtonAction();

        // Bind the 'disabled' property of the saveButton to the emptiness of the memorySetName and priorityLevel fields
        saveButton.disableProperty().bind(
                memorySetName.textProperty().isEmpty()
                        .or(priorityLevel.valueProperty().isNull())
        );
    }


    @FXML
    private void handleSearchButtonAction() {
        // Implement the logic for the search button here
    }

    @FXML
    private void handleRemoveButtonAction(ActionEvent actionEvent) {
        // Retrieve the clicked button from the event source
        Button clickedRemoveButton = (Button) actionEvent.getSource();

        // Assuming each row is an HBox, find the parent HBox for the clicked button
        HBox rowContainer = (HBox) clickedRemoveButton.getParent();

        // Find the TextField for the key within this HBox (assuming it's the second child)
        TextField keyField = (TextField) rowContainer.getChildren().get(1);

        // Remove the key from the cache
        keyValueCache.remove(keyField.getText());

        // Remove the entire HBox (row) from the GridPane
        keyValuePairsContainer.getChildren().remove(rowContainer);
    }




    @FXML
    private void handleAddMoreRowsButtonAction() {
        TextField keyField = new TextField();
        TextField valueField = new TextField();
        Button searchButton = new Button("Search");
        Button removeButton = new Button("X");
        removeButton.setOnAction(this::handleRemoveButtonAction);

        // Listeners to update the cache
        keyField.textProperty().addListener((obs, oldText, newText) -> {
            keyValueCache.put(newText, valueField.getText());
        });

        valueField.textProperty().addListener((obs, oldText, newText) -> {
            keyValueCache.put(keyField.getText(), newText);
        });

        HBox container = new HBox(5); // 5 is spacing between elements
        container.getChildren().addAll(searchButton, keyField, valueField, removeButton);

        // Add them to the GridPane
        int rowIndex = keyValuePairsContainer.getRowCount(); // Assuming you don't have this method, you can replace with a counter.
        keyValuePairsContainer.addRow(rowIndex, container);
    }



    @FXML
    private void handleSaveButtonAction() {
        // Clear the existing cache
        keyValueCache.clear();

        // Iterate over all rows in the GridPane and extract key-value pairs
        for (Node node : keyValuePairsContainer.getChildren()) {
            if (node instanceof HBox) {
                HBox row = (HBox) node;
                TextField keyField = (TextField) row.getChildren().get(1);
                TextField valueField = (TextField) row.getChildren().get(2);
                keyValueCache.put(keyField.getText(), valueField.getText());
            }
        }

        String setName = memorySetName.getText();
        PriorityLevel setPriority = PriorityLevel.valueOf(priorityLevel.getSelectionModel().getSelectedItem());

        MemorySet memorySet = new MemorySet(setName, keyValueCache, setPriority);

        memorySetService.addMemorySetToUser(memorySet);
        ((Node) (saveButton)).getScene().getWindow().hide();
        dashboardController.refreshUI();
    }



}
