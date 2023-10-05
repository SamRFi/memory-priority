package memorypriority.domain;

import memorypriority.util.MemoryPriorityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class AutoRehearsalTest {

    private MemoryCollection memoryCollection;

    @BeforeEach
    public void setUp() {
        Set<MemorySet> memorySets = new HashSet<>();
        memorySets.add(new MemorySet("Set1", new HashMap<>(), PriorityLevel.HIGH));
        memorySets.add(new MemorySet("Set2", new HashMap<>(), PriorityLevel.MEDIUM));
        memorySets.add(new MemorySet("Set3", new HashMap<>(), PriorityLevel.LOW));

        memoryCollection = new MemoryCollection(memorySets);
    }

    @Test
    public void testAutoRehearse() {
        MemorySet memorySet = memoryCollection.getAutoRehearsalSet();

        assertNotNull(memorySet, "Expected non-null memory set");
        assertEquals(new Date().getTime(), memorySet.getLastTimeRehearsed().getTime(), 1000, "Expected the last time rehearsed to be updated to the current time");
    }

    @Test
    public void testAutoRehearseWithEmptyMemoryCollection() {
        memoryCollection.setMemorySets(new HashSet<>()); // set empty memory set

        assertThrows(MemoryPriorityException.class, () -> memoryCollection.getAutoRehearsalSet(), "Expected MemoryPriorityException when no memory sets to be rehearsed");
    }

}