package memorypriority.service;

import io.vertx.core.Future;
import memorypriority.data.AsyncVerseRepository;
import memorypriority.data.MemorySetRepository;
import memorypriority.data.Repositories;
import memorypriority.domain.MemoryCollection;
import memorypriority.domain.MemorySet;
import memorypriority.domain.PriorityLevel;
import memorypriority.util.MemoryPriorityException;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MemorySetService {

    public static final Logger LOGGER = Logger.getLogger(MemorySetService.class.getName());
    private final String loggedInUser;
    private final MemorySetRepository memorySetRepository;
    private final AsyncVerseRepository asyncVerseRepository;

    public MemorySetService(String loggedInUser) {
        this.memorySetRepository = Repositories.getMemorySetRepository();
        this.asyncVerseRepository = Repositories.getAsyncVerseRepository();
        this.loggedInUser = loggedInUser;
    }

    public MemoryCollection getMemoryCollectionOfUser() {
        return memorySetRepository.getMemoryCollectionOfUser(loggedInUser);
    }

    public void addMemorySetToUser(MemorySet memorySet) {

        memorySetRepository.addMemorySetToUser(loggedInUser, memorySet);
        LOGGER.log(Level.INFO, "added memory set to user collection" + memorySet.getPairList().toString(), memorySet);
    }

    public void removeMemorySet(MemorySet memorySet) {
        try {
            memorySetRepository.removeMemorySet(memorySet.getId());
            LOGGER.log(Level.INFO, "removed memory set from user collection" + memorySet.getPairList().toString(), memorySet);
        } catch (Exception e) {
            throw new MemoryPriorityException("Error removing memory set", e);
        }
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

    public void rehearseMemorySet(MemorySet memorySet) {
        MemorySet memorySetToBeSaved = findMemorySetById(memorySet.getId());
        memorySetRepository.removeMemorySet(memorySet.getId());
        memorySetToBeSaved.rehearsedNow();
        addMemorySetToUser(memorySetToBeSaved);
    }

    public Future<String> getVerse(String verseReference) {
        return asyncVerseRepository.getVerse(verseReference);
    }

    private MemorySet findMemorySetById(int id) {
        MemoryCollection memoryCollection = getMemoryCollectionOfUser();
        for (MemorySet memorySet : memoryCollection.getMemorySets()) {
            if (memorySet.getId() == id) {
                return memorySet;
            }
        }
        return null;
    }

}