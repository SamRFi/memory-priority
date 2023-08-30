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

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AddMemorySetController {

    public static final Logger LOGGER = Logger.getLogger(AddMemorySetController.class.getName());

    ResourceBundle rb = ResourceBundle.getBundle("internationalization/text", Locale.forLanguageTag("en"));
    private MemorySetService memorySetService;
    private DashboardController dashboardController;

    private List<Map.Entry<String, String>> keyValueCache = new ArrayList<>();


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
        Button searchButton = new Button(rb.getString("searchButton"));
        searchButton.setOnAction(this::handleSearchButtonAction);
        Button removeButton = new Button("X");
        removeButton.setOnAction(this::handleRemoveButtonAction);

        // Listeners to update the cache
        keyField.textProperty().addListener((obs, oldText, newText) -> {
            // Create a new entry with the key and value from the text fields
            Map.Entry<String, String> entry = new AbstractMap.SimpleEntry<>(newText, valueField.getText());
            // Remove the old entry from the cache if it exists
            keyValueCache.removeIf(e -> e.getKey().equals(oldText));
            // Add the new entry to the cache
            keyValueCache.add(entry);
        });

        valueField.textProperty().addListener((obs, oldText, newText) -> {
            // Create a new entry with the key and value from the text fields
            Map.Entry<String, String> entry = new AbstractMap.SimpleEntry<>(keyField.getText(), newText);
            // Remove the old entry from the cache if it exists
            keyValueCache.removeIf(e -> e.getValue().equals(oldText));
            // Add the new entry to the cache
            keyValueCache.add(entry);
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

                // Create a new entry with the key and value from the text fields
                Map.Entry<String, String> entry = new AbstractMap.SimpleEntry<>(keyField.getText(), valueField.getText());

                // Add the entry to the cache
                keyValueCache.add(entry);
            }
        }

        String setName = memorySetName.getText();
        PriorityLevel setPriority = PriorityLevel.valueOf(priorityLevel.getSelectionModel().getSelectedItem());

        // Use the list instead of the map to create a new MemorySet object
        MemorySet memorySet = new MemorySet(setName, keyValueCache, setPriority);

        memorySetService.addMemorySetToUser(memorySet);
        ((Node) (saveButton)).getScene().getWindow().hide();
        dashboardController.refreshUI();
    }


    @FXML
    private void handleSearchButtonAction(ActionEvent actionEvent) {
        // Retrieve the clicked button from the event source
        Button clickedSearchButton = (Button) actionEvent.getSource();

        // Assuming each row is an HBox, find the parent HBox for the clicked button
        HBox rowContainer = (HBox) clickedSearchButton.getParent();

        // Find the TextField for the key within this HBox (assuming it's the second child)
        TextField keyField = (TextField) rowContainer.getChildren().get(1);
        // Find the TextField for the value within this HBox (assuming it's the third child)
        TextField valueField = (TextField) rowContainer.getChildren().get(2);

        String verseReference = keyField.getText();

        // Use MemorySetService to get the verse asynchronously
        memorySetService.getVerse(verseReference).onSuccess(verseText -> {
            valueField.setText(verseText); // set the verse text in the value field
        }).onFailure(ex -> {
            // Handle error (for now, we'll just print it, but you could display an error message to the user)
            valueField.setText("Verse not found!");
        });
    }


}
