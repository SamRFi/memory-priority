package memorypriority.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import memorypriority.domain.MemoryCollection;
import memorypriority.domain.MemorySet;
import memorypriority.domain.PriorityLevel;
import memorypriority.service.MemorySetService;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public class DashboardController {

    public static final Logger LOGGER = Logger.getLogger(DashboardController.class.getName());

    ResourceBundle rb = ResourceBundle.getBundle("internationalization/text", Locale.forLanguageTag("en"));
    @FXML
    public Button reviewSetsButton;
    @FXML
    private VBox highPriorityColumn;
    @FXML
    private VBox mediumPriorityColumn;
    @FXML
    private VBox lowPriorityColumn;

    @FXML
    private VBox noPriorityColumn;

    @FXML
    private Button backButton;

    @FXML
    public void handleReviewSetsButtonClick(ActionEvent event) {
        rehearseMemorySet(memorySetService.autoRehearse());
    }

    private MemorySetService memorySetService;

    public void setMemorySetService(MemorySetService memorySetService) {
        this.memorySetService = memorySetService;
    }

    @FXML
    public void handleBackButtonClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            loader.setResources(ResourceBundle.getBundle("internationalization/text", Locale.forLanguageTag("en")));
            Parent root = loader.load();
            Stage stage = (Stage) backButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void populateMemorySets() {
        MemoryCollection memoryCollection = memorySetService.getMemoryCollectionOfUser();
        Set<MemorySet> memorySetSet = memoryCollection.getMemorySets();

        List<MemorySet> memorySets = new ArrayList<>(memorySetSet);
        memorySets.sort(Comparator.comparing(MemorySet::getLastTimeRehearsed));

        for (MemorySet memorySet : memorySets) {
            switch (memorySet.getPriorityLevel()) {
                case HIGH:
                    highPriorityColumn.getChildren().add(createMemorySetBox(memorySet));
                    break;
                case MEDIUM:
                    mediumPriorityColumn.getChildren().add(createMemorySetBox(memorySet));
                    break;
                case LOW:
                    lowPriorityColumn.getChildren().add(createMemorySetBox(memorySet));
                    break;
                case NONE:
                    noPriorityColumn.getChildren().add(createMemorySetBox(memorySet));
                    break;
            }
        }
    }

    private GridPane createMemorySetBox(MemorySet memorySet) {
        Label nameLabel = new Label(memorySet.getName());
        nameLabel.setMaxWidth(Double.MAX_VALUE);
        nameLabel.setAlignment(Pos.CENTER);
        GridPane.setHgrow(nameLabel, Priority.ALWAYS);

        Tooltip tooltip = new Tooltip(memorySet.getName());
        Tooltip.install(nameLabel, tooltip);

        Button rehearseButton = new Button(rb.getString("rehearseButton"));
        rehearseButton.setMinWidth(80);
        rehearseButton.setOnAction(event -> rehearseMemorySet(memorySet));
        Button increasePriorityButton = new Button("<");
        increasePriorityButton.setMinWidth(25);
        increasePriorityButton.setOnAction(event -> increasePriority(memorySet));
        Button decreasePriorityButton = new Button(">");
        decreasePriorityButton.setMinWidth(25);
        decreasePriorityButton.setOnAction(event -> decreasePriority(memorySet));

        Button deleteButton = new Button("X");
        deleteButton.setMinWidth(25);
        deleteButton.setOnAction(event -> deleteMemorySet(memorySet));

        GridPane memorySetBox = new GridPane();
        memorySetBox.setHgap(10);
        memorySetBox.setPrefWidth(600);
        memorySetBox.add(increasePriorityButton, 0, 0);
        memorySetBox.add(nameLabel, 1, 0);
        memorySetBox.add(rehearseButton, 2, 0);
        memorySetBox.add(deleteButton, 4, 0);
        memorySetBox.add(decreasePriorityButton, 3, 0);

        memorySetBox.setStyle("-fx-border-color: white; -fx-border-width: 1; -fx-border-radius: 10; -fx-padding: 5");

        return memorySetBox;
    }

    private void deleteMemorySet(MemorySet memorySet) {
        //todo: localization
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Memory Set");
        alert.setHeaderText("Are you sure you want to delete this memory set?");
        alert.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            memorySetService.removeMemorySet(memorySet);
            refreshUI();
        }
    }

    private void rehearseMemorySet(MemorySet memorySet) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/rehearsalScreen.fxml"));
        loader.setResources(ResourceBundle.getBundle("internationalization/text", Locale.forLanguageTag("en")));
        Parent dialogContent;
        try {
            dialogContent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        RehearsalController rehearsalController = loader.getController();
        rehearsalController.setMemorySet(memorySet);
        rehearsalController.setMemorySetService(memorySetService);
        rehearsalController.setDashboardController(this);

        Dialog<Void> dialog = new Dialog<>();
        rehearsalController.setDialog(dialog);

        dialog.setTitle(memorySet.getName());
        dialog.getDialogPane().setContent(dialogContent);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);

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
        // Instead of clearing all children, update necessary nodes only
        updatePriorityColumn(highPriorityColumn, PriorityLevel.HIGH);
        updatePriorityColumn(mediumPriorityColumn, PriorityLevel.MEDIUM);
        updatePriorityColumn(lowPriorityColumn, PriorityLevel.LOW);
        updatePriorityColumn(noPriorityColumn, PriorityLevel.NONE);
    }

    private void updatePriorityColumn(VBox column, PriorityLevel priorityLevel) {
        // Remove all but the first child (the label)
        if (column.getChildren().size() > 1) {
            column.getChildren().subList(1, column.getChildren().size()).clear();
        }

        // Add new memory sets
        memorySetService.getMemoryCollectionOfUser().getMemorySets().stream()
                .filter(memorySet -> memorySet.getPriorityLevel() == priorityLevel)
                .sorted(Comparator.comparing(MemorySet::getLastTimeRehearsed))
                .forEach(memorySet -> column.getChildren().add(createMemorySetBox(memorySet)));
    }



    private Label createPriorityLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 20");
        label.setPrefWidth(150);
        label.setPrefHeight(30);
        return label;
    }

    public void showAddMemorySetDialog() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/addMemorySet.fxml"));
        loader.setResources(ResourceBundle.getBundle("internationalization/text", Locale.forLanguageTag("en")));
        Parent dialogContent;
        try {
            dialogContent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        AddMemorySetController addMemorySetController = loader.getController();
        addMemorySetController.setMemorySetService(memorySetService);
        addMemorySetController.setDashboardController(this);

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Add Memory Set");
        dialog.getDialogPane().setContent(dialogContent);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);

        dialog.showAndWait();
    }

    public void handleAddSet(ActionEvent actionEvent) {
        showAddMemorySetDialog();
    }
}