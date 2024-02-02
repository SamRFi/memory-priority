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
            default:
                throw new IllegalStateException("Unexpected value: " + memorySet.getPriorityLevel());
        }
        long timeSinceLastRehearsed = System.currentTimeMillis() - memorySet.getLastTimeRehearsed().getTime();
        return priorityScore + timeSinceLastRehearsed;
    }

    private MemorySet getNextSetToRehearse() {
        long currentTime = System.currentTimeMillis();

        LOGGER.info("Starting getNextSetToRehearse - Current Time: " + new Timestamp(currentTime));

        // Phase 1: Select sets rehearsed beyond the interval, prioritized by score.
        MemorySet nextSet = memorySets.stream()
                .filter(set -> {
                    boolean isEligible = set.getLastTimeRehearsed().getTime() + rehearsalInterval < currentTime;
                    // Log detailed information for each set when checking for Phase 1 eligibility
                    LOGGER.info("Checking set: " + set.getName() + " for Phase 1, Last Rehearsed: " + set.getLastTimeRehearsed()
                            + ", Eligible: " + isEligible + ", Rehearsal Interval Ends At: "
                            + new Timestamp(set.getLastTimeRehearsed().getTime() + rehearsalInterval));
                    return isEligible;
                })
                .max(Comparator.comparingLong(this::calculateScore))
                .orElse(null);

        if (nextSet != null) {
            LOGGER.info("Found a set in Phase 1: " + nextSet.getName() + ", Last Rehearsed: " + nextSet.getLastTimeRehearsed());
            return nextSet;
        } else {
            LOGGER.info("No set found in Phase 1, moving to Phase 2");
        }

        // Phase 2: If no set is found in Phase 1, select from within the interval based on time since last rehearsal.
        nextSet = memorySets.stream()
                .filter(set -> {
                    boolean isEligible = set.getLastTimeRehearsed().getTime() + rehearsalInterval >= currentTime;
                    LOGGER.info("Checking set: " + set.getName() + " for Phase 2, Last Rehearsed: " + set.getLastTimeRehearsed()
                            + ", Eligible: " + isEligible);
                    return isEligible;
                })
                .max(Comparator.comparingLong(set -> currentTime - set.getLastTimeRehearsed().getTime()))
                .orElse(null);

        // Log the outcome of Phase 2
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
