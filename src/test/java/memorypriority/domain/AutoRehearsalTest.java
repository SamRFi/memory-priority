package memorypriority.domain;

import memorypriority.util.MemoryPriorityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class AutoRehearsalTest {

    private MemoryCollection memoryCollection;

    @BeforeEach
    public void setUp() {
        Set<MemorySet> memorySets = new HashSet<>();

        Timestamp lastRehearsedTimeForSet1 = new Timestamp(System.currentTimeMillis() - 3 * 3600 * 1000); // 3 hours ago
        MemorySet set1 = new MemorySet("Set1", new ArrayList<>(), PriorityLevel.HIGH, lastRehearsedTimeForSet1);

        Timestamp lastRehearsedTimeForSet2 = new Timestamp(System.currentTimeMillis() - 3600 * 1000); // 1 hour ago
        MemorySet set2 = new MemorySet("Set2", new ArrayList<>(), PriorityLevel.MEDIUM, lastRehearsedTimeForSet2);

        Timestamp lastRehearsedTimeForSet3 = new Timestamp(System.currentTimeMillis() - 2 * 3600 * 1000); // 2 hours ago
        MemorySet set3 = new MemorySet("Set3", new ArrayList<>(), PriorityLevel.LOW, lastRehearsedTimeForSet3);

        memorySets.add(set1);
        memorySets.add(set2);
        memorySets.add(set3);

        memoryCollection = new MemoryCollection(memorySets);
    }


    @Test
    void testAutoRehearseWithEligibleSets() {
        MemorySet memorySet = memoryCollection.getAutoRehearsalSet();

        assertNotNull(memorySet, "Expected non-null memory set");
        assertTrue(memorySet.getLastTimeRehearsed().before(new Timestamp(System.currentTimeMillis() - 7200)),
                "Expected the selected memory set to have a last rehearsed time more than 2 hours ago");
    }

    @Test
    void testAutoRehearseWithEmptyMemoryCollection() {
        memoryCollection.setMemorySets(new HashSet<>()); // set empty memory set

        assertThrows(MemoryPriorityException.class, () -> memoryCollection.getAutoRehearsalSet(),
                "Expected MemoryPriorityException when no memory sets to be rehearsed");
    }

    @Test
    void testRehearsalInterval() {
        Set<MemorySet> memorySets = new HashSet<>();
        Timestamp lastRehearsedTimeForSet1 = new Timestamp(System.currentTimeMillis() - 3600 * 1000); // 1 hour ago
        MemorySet set1 = new MemorySet("Set1", new ArrayList<>(), PriorityLevel.HIGH, lastRehearsedTimeForSet1);

        Timestamp lastRehearsedTimeForSet2 = new Timestamp(System.currentTimeMillis() - 7201 * 1000); // More than 2 hours ago
        MemorySet set2 = new MemorySet("Set2", new ArrayList<>(), PriorityLevel.HIGH, lastRehearsedTimeForSet2);

        memorySets.add(set1);
        memorySets.add(set2);

        memoryCollection = new MemoryCollection(memorySets);

        MemorySet memorySet = memoryCollection.getAutoRehearsalSet();

        assertNotNull(memorySet, "Expected non-null memory set");
        assertEquals(set2, memorySet, "Expected the set with the longest time since last rehearsed to be returned");
    }

    @Test
    void testSelectionWithinRehearsalInterval() {
        memoryCollection.setMemorySets(new HashSet<>());
        Timestamp lastRehearsedWithin1 = new Timestamp(System.currentTimeMillis() - 30 * 60 * 1000); // 30 minutes ago
        MemorySet setWithin1 = new MemorySet("Within1", new ArrayList<>(), PriorityLevel.HIGH, lastRehearsedWithin1);

        Timestamp lastRehearsedWithin2 = new Timestamp(System.currentTimeMillis() - 40 * 60 * 1000); // 40 minutes ago, corrected comment to match code
        MemorySet setWithin2 = new MemorySet("Within2", new ArrayList<>(), PriorityLevel.LOW, lastRehearsedWithin2);

        memoryCollection.getMemorySets().add(setWithin1);
        memoryCollection.getMemorySets().add(setWithin2);

        MemorySet selectedSet = memoryCollection.getAutoRehearsalSet();

        assertNotNull(selectedSet, "Expected a non-null memory set to be selected.");
        assertEquals("Within2", selectedSet.getName(), "Expected the set that was last rehearsed the longest time ago within the interval to be selected.");
    }
}