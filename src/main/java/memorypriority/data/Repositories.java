package memorypriority.data;

import memorypriority.util.MemoryPriorityException;

import java.sql.SQLException;

public class Repositories {
    private static JdbcAuthorizationRepository jdbcAuthorizationRepository = new JdbcAuthorizationRepository();
    private static MemorySetRepository memorySetRepository;

    static {
        try {
            memorySetRepository = new JdbcMemorySetRepository();
        } catch (SQLException e) {
            throw new MemoryPriorityException("Failed to get connection to the database", e);
        }
    }

    public static JdbcAuthorizationRepository getAuthorizationRepository() {
        return jdbcAuthorizationRepository;
    }

    public static MemorySetRepository getMemorySetRepository() {
        return memorySetRepository;
    }
}