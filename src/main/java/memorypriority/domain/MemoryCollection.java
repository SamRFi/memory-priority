package memorypriority.domain;

import memorypriority.util.Config;
import memorypriority.util.MemoryPriorityException;

import java.util.Comparator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MemoryCollection {

    public static final Logger LOGGER = Logger.getLogger(Config.class.getName());
    private Set<MemorySet> memorySets;

    private long calculateScore(MemorySet memorySet) {
        long priorityScore;
        switch (memorySet.getPriorityLevel()) {
            case HIGH:
                priorityScore = 300_000_000L;
                break;
            case MEDIUM:
                priorityScore = 200_000_000L;
                break;
            case LOW:
                priorityScore = 100_000_000L;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + memorySet.getPriorityLevel());
        }
        long timeSinceLastRehearsed = System.currentTimeMillis() - memorySet.getLastTimeRehearsed().getTime();
        return priorityScore - timeSinceLastRehearsed;
    }


    private MemorySet getNextSetToRehearse() {
        return memorySets.stream()
                .max(Comparator.comparingLong(this::calculateScore))
                .orElse(null);
    }

    public MemorySet autoRehearse() {
        MemorySet nextSet = getNextSetToRehearse();
        if (nextSet != null) {
            nextSet.rehearsedNow();
            return nextSet;
        } else {
            throw new MemoryPriorityException("No memory sets to be rehearsed");
        }
    }

    public Set<MemorySet> getMemorySets() {
        return memorySets;
    }

    public void setMemorySets(Set<MemorySet> memorySets) {
        this.memorySets = memorySets;
    }
}
