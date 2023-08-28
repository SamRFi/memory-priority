package memorypriority.data;

import memorypriority.domain.MemoryCollection;
import memorypriority.domain.MemorySet;
import memorypriority.domain.PriorityLevel;
import memorypriority.util.MemoryPriorityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class FileMemorySetRepositoryTest {

    private static final String filePath = "./data/memory_data.txt";

    private FileMemorySetRepository repository;

    @BeforeEach
    public void setUp() {
        repository = new FileMemorySetRepository();
    }


    @Test
    public void testRemoveMemorySet() throws MemoryPriorityException {
        MemorySet memorySet = new MemorySet(999999, "Test Memory Set", new HashMap<>(), PriorityLevel.LOW, new java.sql.Date(System.currentTimeMillis()));
        repository.addMemorySetToUser("test_user", memorySet);

        repository.removeMemorySet(999999);

        MemoryCollection memoryCollection = repository.getMemoryCollectionOfUser("test_user");

        boolean memorySetExists = false;
        for (MemorySet set : memoryCollection.getMemorySets()) {
            if (set.getId() == 999999) {
                memorySetExists = true;
                break;
            }
        }

        assertFalse(memorySetExists);
    }


}