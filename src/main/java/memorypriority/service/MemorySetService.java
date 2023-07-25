package memorypriority.service;

import memorypriority.data.MemorySetRepository;
import memorypriority.data.Repositories;

public class MemorySetService {
    private final MemorySetRepository memorySetRepository;

    public MemorySetService() {
        this.memorySetRepository = Repositories.getMemorySetRepository();
    }
}
