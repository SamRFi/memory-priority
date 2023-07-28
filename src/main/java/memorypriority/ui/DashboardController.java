package memorypriority.ui;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import memorypriority.domain.MemoryCollection;
import memorypriority.domain.MemorySet;
import memorypriority.domain.User;
import memorypriority.service.MemorySetService;

import java.util.Set;

public class DashboardController {

    @FXML
    private VBox highPriorityColumn;
    @FXML
    private VBox mediumPriorityColumn;
    @FXML
    private VBox lowPriorityColumn;

    private MemorySetService memorySetService;

    public DashboardController() {
        this.memorySetService = new MemorySetService(new User("root", "root"));
    }

    @FXML
    public void initialize() {
        populateMemorySets();
    }

    public void populateMemorySets() {
        MemoryCollection memoryCollection = memorySetService.getMemoryCollectionOfUser();
        Set<MemorySet> memorySets = memoryCollection.getMemorySets();

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

        Button rehearseButton = new Button("Rehearse");
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
        // Implement rehearse logic
    }

    private void increasePriority(MemorySet memorySet) {
        // Implement increase priority logic
    }

    private void decreasePriority(MemorySet memorySet) {
        // Implement decrease priority logic
    }
}
