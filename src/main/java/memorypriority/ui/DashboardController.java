package memorypriority.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import memorypriority.domain.MemoryCollection;
import memorypriority.domain.MemorySet;
import memorypriority.service.MemorySetService;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public class DashboardController {

    public static final Logger LOGGER = Logger.getLogger(DashboardController.class.getName());

    ResourceBundle rb = ResourceBundle.getBundle("internationalization/text", Locale.forLanguageTag("nl"));

    @FXML
    private VBox highPriorityColumn;
    @FXML
    private VBox mediumPriorityColumn;
    @FXML
    private VBox lowPriorityColumn;

    private MemorySetService memorySetService;

    public DashboardController() {
        this.memorySetService = new MemorySetService("root");
    }

    @FXML
    public void initialize() {
        populateMemorySets();
    }

    public void populateMemorySets() {
        MemoryCollection memoryCollection = memorySetService.getMemoryCollectionOfUser();
        Set<MemorySet> memorySetSet = memoryCollection.getMemorySets();

        List<MemorySet> memorySets = new ArrayList<>(memorySetSet);
        memorySets.sort(Comparator.comparing(MemorySet::getLastTimeRehearsed));

        for (MemorySet memorySet : memorySets) {
            switch (memorySet.getPriorityLevel()) {
                case HIGH:
                    highPriorityColumn.getChildren().add(1, createMemorySetBox(memorySet));
                    break;
                case MEDIUM:
                    mediumPriorityColumn.getChildren().add(1, createMemorySetBox(memorySet));
                    break;
                case LOW:
                    lowPriorityColumn.getChildren().add(1, createMemorySetBox(memorySet));
                    break;
            }
        }
    }


    private GridPane createMemorySetBox(MemorySet memorySet) {
        Label nameLabel = new Label(memorySet.getName());
        nameLabel.setMaxWidth(Double.MAX_VALUE); // Allow the label to grow as much as possible
        nameLabel.setAlignment(Pos.CENTER);
        GridPane.setHgrow(nameLabel, Priority.ALWAYS); // Allow the label to grow as much as possible

        // Add a tooltip to show the full name when hovering over the label
        Tooltip tooltip = new Tooltip(memorySet.getName());
        Tooltip.install(nameLabel, tooltip);

        Button rehearseButton = new Button(rb.getString("rehearseButton"));
        rehearseButton.setMinWidth(80); // Set minimum width for the button
        rehearseButton.setOnAction(event -> rehearseMemorySet(memorySet));
        Button increasePriorityButton = new Button("<");
        increasePriorityButton.setMinWidth(25); // Set minimum width for the button
        increasePriorityButton.setOnAction(event -> increasePriority(memorySet));
        Button decreasePriorityButton = new Button(">");
        decreasePriorityButton.setMinWidth(25); // Set minimum width for the button
        decreasePriorityButton.setOnAction(event -> decreasePriority(memorySet));

        // Use GridPane instead of HBox
        GridPane memorySetBox = new GridPane();
        memorySetBox.setHgap(10);
        memorySetBox.setPrefWidth(600); // Set the preferred width of the box
        memorySetBox.add(increasePriorityButton, 0, 0);
        memorySetBox.add(nameLabel, 1, 0);
        memorySetBox.add(rehearseButton, 2, 0);
        memorySetBox.add(decreasePriorityButton, 3, 0);

        memorySetBox.setStyle("-fx-border-color: white; -fx-border-width: 1; -fx-border-radius: 10; -fx-padding: 5");

        return memorySetBox;
    }

    private void rehearseMemorySet(MemorySet memorySet) {
        // Load the FXML for the rehearsal screen
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/rehearsalScreen.fxml"));
        loader.setResources(ResourceBundle.getBundle("internationalization/text", Locale.forLanguageTag("en")));
        Parent dialogContent;
        try {
            dialogContent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Get the controller and set the MemorySet and MemorySetService
        RehearsalController rehearsalController = loader.getController();
        rehearsalController.setMemorySet(memorySet);
        rehearsalController.setMemorySetService(memorySetService);
        rehearsalController.setDashboardController(this);

        // Create the dialog
        Dialog<Void> dialog = new Dialog<>();
        rehearsalController.setDialog(dialog);

        dialog.setTitle("Rehearse Memory Set");
        dialog.getDialogPane().setContent(dialogContent);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);

        // Show the dialog
        dialog.showAndWait();
    }


    private void increasePriority(MemorySet memorySet) {
        memorySetService.increasePriority(memorySet);
        refreshUI();
    }

    private void decreasePriority(MemorySet memorySet) {
        memorySetService.decreasePriority(memorySet);
        refreshUI();
    }

    public void refreshUI() {
        highPriorityColumn.getChildren().clear();
        mediumPriorityColumn.getChildren().clear();
        lowPriorityColumn.getChildren().clear();

        highPriorityColumn.getChildren().add(createPriorityLabel(rb.getString("highPriority")));
        mediumPriorityColumn.getChildren().add(createPriorityLabel(rb.getString("mediumPriority")));
        lowPriorityColumn.getChildren().add(createPriorityLabel(rb.getString("lowPriority")));

        populateMemorySets();
    }


    private Label createPriorityLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 20");
        label.setPrefWidth(150);
        label.setPrefHeight(30);
        return label;
    }


    public void showAddMemorySetDialog() {
        // Load the FXML for the dialog
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/addMemorySet.fxml"));
        loader.setResources(ResourceBundle.getBundle("internationalization/text", Locale.forLanguageTag("en")));
        Parent dialogContent;
        try {
            dialogContent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Get the controller and set the MemorySetService
        AddMemorySetController addMemorySetController = loader.getController();
        addMemorySetController.setMemorySetService(memorySetService);
        addMemorySetController.setDashboardController(this);

        // Create the dialog
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Add Memory Set");
        dialog.getDialogPane().setContent(dialogContent);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);

        // Show the dialog
        dialog.showAndWait();
    }


    public void handleAddSet(ActionEvent actionEvent) {
        showAddMemorySetDialog();
    }
}
