package memorypriority.data;

import memorypriority.domain.MemoryCollection;
import memorypriority.domain.MemorySet;

public interface MemorySetRepository {
    MemoryCollection getMemoryCollectionOfUser(String username);
    void addMemorySetToUser(String username, MemorySet memorySet);
}
