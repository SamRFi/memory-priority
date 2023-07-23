package memorypriority.data;

import memorypriority.domain.MemoryCollection;

public interface MemorySetRepository {
    MemoryCollection getMemoryCollectionOfUser(String username);
    void addMemorySetToUser(String username);
}
