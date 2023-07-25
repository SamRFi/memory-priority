package memorypriority.service;

import memorypriority.data.MemorySetRepository;
import memorypriority.data.Repositories;
import memorypriority.domain.MemoryCollection;
import memorypriority.domain.MemorySet;
import memorypriority.domain.User;

public class MemorySetService {
    private final User loggedInUser;
    private final MemorySetRepository memorySetRepository;

    public MemorySetService(User loggedInUser) {
        this.memorySetRepository = Repositories.getMemorySetRepository();
        this.loggedInUser = loggedInUser;
    }

    public MemoryCollection getMemoryCollectionOfUser() {
        return memorySetRepository.getMemoryCollectionOfUser(loggedInUser.getUsername());
    }

    public void addMemorySetToUser(MemorySet memorySet) {
        memorySetRepository.addMemorySetToUser(loggedInUser.getUsername(), memorySet);
    }
}
