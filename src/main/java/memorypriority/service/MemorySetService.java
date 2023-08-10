package memorypriority.service;

import memorypriority.data.MemorySetRepository;
import memorypriority.data.Repositories;
import memorypriority.domain.MemoryCollection;
import memorypriority.domain.MemorySet;
import memorypriority.domain.PriorityLevel;
import memorypriority.domain.User;

public class MemorySetService {
    private final User loggedInUser;
    private final MemorySetRepository memorySetRepository;

    public MemorySetService(User loggedInUser) {
        this.memorySetRepository = Repositories.getMemorySetRepository();
        this.loggedInUser = loggedInUser;
    }

    public MemoryCollection getMemoryCollectionOfUser() {
        return memorySetRepository.getMemoryCollectionOfUser(loggedInUser.getUsername());
    }

    public void addMemorySetToUser(MemorySet memorySet) {
        memorySetRepository.addMemorySetToUser(loggedInUser.getUsername(), memorySet);
    }

    public void increasePriority(MemorySet memorySet) {
        PriorityLevel currentPriority = memorySet.getPriorityLevel();

        switch(currentPriority) {
            case LOW:
                memorySet.setPriorityLevel(PriorityLevel.MEDIUM);
                break;
            case MEDIUM:
                memorySet.setPriorityLevel(PriorityLevel.HIGH);
                break;
            case HIGH:
                // Already at highest priority, so no change
                return;
        }

        memorySetRepository.changePriority(memorySet, memorySet.getPriorityLevel());
    }

    public void decreasePriority(MemorySet memorySet) {
        PriorityLevel currentPriority = memorySet.getPriorityLevel();

        switch(currentPriority) {
            case HIGH:
                memorySet.setPriorityLevel(PriorityLevel.MEDIUM);
                break;
            case MEDIUM:
                memorySet.setPriorityLevel(PriorityLevel.LOW);
                break;
            case LOW:
                // Already at lowest priority, so no change
                return;
        }

        memorySetRepository.changePriority(memorySet, memorySet.getPriorityLevel());
    }
}
