package memorypriority.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
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

    // This should be called after the user logs in
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

    private HBox createMemorySetBox(MemorySet memorySet) {
        Label nameLabel = new Label(memorySet.getName());
        Button rehearseButton = new Button("Rehearse");
        rehearseButton.setOnAction(event -> rehearseMemorySet(memorySet));
        Button increasePriorityButton = new Button("<");
        increasePriorityButton.setOnAction(event -> increasePriority(memorySet));
        Button decreasePriorityButton = new Button(">");
        decreasePriorityButton.setOnAction(event -> decreasePriority(memorySet));

        HBox memorySetBox = new HBox(nameLabel, rehearseButton, increasePriorityButton, decreasePriorityButton);
        memorySetBox.setSpacing(10);

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
