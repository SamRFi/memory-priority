package memorypriority.service;

import io.vertx.core.Future;
import memorypriority.data.AsyncVerseRepository;
import memorypriority.data.MemorySetRepository;
import memorypriority.data.Repositories;
import memorypriority.domain.MemoryCollection;
import memorypriority.domain.MemorySet;
import memorypriority.domain.PriorityLevel;
import memorypriority.domain.User;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MemorySetService {

    public static final Logger LOGGER = Logger.getLogger(MemorySetService.class.getName());
    private final User loggedInUser;
    private final MemorySetRepository memorySetRepository;
    private final AsyncVerseRepository asyncVerseRepository;

    public MemorySetService(User loggedInUser) {
        this.memorySetRepository = Repositories.getMemorySetRepository();
        this.asyncVerseRepository = Repositories.getAsyncVerseRepository();
        this.loggedInUser = loggedInUser;
    }

    public MemoryCollection getMemoryCollectionOfUser() {
        return memorySetRepository.getMemoryCollectionOfUser(loggedInUser.getUsername());
    }

    public void addMemorySetToUser(MemorySet memorySet) {

        memorySetRepository.addMemorySetToUser(loggedInUser.getUsername(), memorySet);
        LOGGER.log(Level.INFO, "added memory set to user collection" + memorySet.getMemorySet().toString(), memorySet);
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

    public Future<String> getVerse(String verseReference) {
        return asyncVerseRepository.getVerse(verseReference);
    }

}
