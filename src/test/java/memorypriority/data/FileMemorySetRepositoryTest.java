package memorypriority.data;

import memorypriority.domain.MemoryCollection;
import memorypriority.domain.MemorySet;
import memorypriority.domain.PriorityLevel;
import memorypriority.util.MemoryPriorityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;


import static org.junit.jupiter.api.Assertions.*;

class FileMemorySetRepositoryTest {

    private static final String filePath = "./data/memory_data.txt";

    private FileMemorySetRepository repository;

    @BeforeEach
    public void setUp() {
        repository = new FileMemorySetRepository();
    }



}