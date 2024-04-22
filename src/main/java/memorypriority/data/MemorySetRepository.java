package memorypriority.data;

import memorypriority.domain.MemoryCollection;
import memorypriority.domain.MemorySet;
import memorypriority.domain.PriorityLevel;

import java.util.UUID;

public interface MemorySetRepository {
    MemoryCollection getMemoryCollectionOfUser(String username);
    void addMemorySetToUser(String username, MemorySet memorySet);

    void changePriority(String username, MemorySet memorySet, PriorityLevel newPriority);
    void removeMemorySet(String username, UUID id);
}
