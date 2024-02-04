package memorypriority.domain;

import memorypriority.util.MemoryPriorityException;

import java.sql.Timestamp;
import java.util.Comparator;
import java.util.Set;
import java.util.logging.Logger;

public class MemoryCollection {

    public static final Logger LOGGER = Logger.getLogger(MemoryCollection.class.getName());
    private Set<MemorySet> memorySets;

    private final long rehearsalInterval;

    public MemoryCollection(Set<MemorySet> memorySets) {
        this.memorySets = memorySets;
        this.rehearsalInterval = 7200000; // 2 hours in milliseconds
    }

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
            case NONE:
                return 0L;
            default:
                throw new IllegalStateException("Unexpected value: " + memorySet.getPriorityLevel());
        }
        long timeSinceLastRehearsed = System.currentTimeMillis() - memorySet.getLastTimeRehearsed().getTime();
        return priorityScore + timeSinceLastRehearsed;
    }


    private MemorySet getNextSetToRehearse() {
        long currentTime = System.currentTimeMillis();
        LOGGER.info("Starting getNextSetToRehearse - Current Time: " + new Timestamp(currentTime));

        // Phase 1: Exclude sets with PriorityLevel.NONE and select sets rehearsed beyond the interval, prioritized by score.
        MemorySet nextSet = memorySets.stream()
                .filter(set -> set.getPriorityLevel() != PriorityLevel.NONE) // Exclude sets with no priority
                .filter(set -> set.getLastTimeRehearsed().getTime() + rehearsalInterval < currentTime)
                .max(Comparator.comparingLong(this::calculateScore))
                .orElse(null);

        if (nextSet != null) {
            LOGGER.info("Found a set in Phase 1: " + nextSet.getName() + ", Last Rehearsed: " + nextSet.getLastTimeRehearsed());
            return nextSet;
        } else {
            LOGGER.info("No set found in Phase 1, moving to Phase 2");
        }

        // Phase 2
        nextSet = memorySets.stream()
                .filter(set -> set.getPriorityLevel() != PriorityLevel.NONE)
                .filter(set -> set.getLastTimeRehearsed().getTime() + rehearsalInterval >= currentTime)
                .max(Comparator.comparingLong(set -> currentTime - set.getLastTimeRehearsed().getTime()))
                .orElse(null);

        if (nextSet != null) {
            LOGGER.info("Found a set in Phase 2: " + nextSet.getName() + ", Last Rehearsed: " + nextSet.getLastTimeRehearsed());
        } else {
            LOGGER.info("No set found in Phase 2");
        }

        return nextSet;
    }



    public MemorySet getAutoRehearsalSet() {
        MemorySet nextSet = getNextSetToRehearse();
        if (nextSet != null) {
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
