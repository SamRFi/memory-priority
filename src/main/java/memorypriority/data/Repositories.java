package memorypriority.data;

import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import memorypriority.util.MemoryPriorityException;

import java.sql.SQLException;

public class Repositories {

    private static final int PORT = 443;
    private static final String HOST = "bible-api.com";
    private static final WebClient WEB_CLIENT =  WebClient.create(Vertx.vertx());
    private static final boolean ENABLE_SSL = true;

    private static final JdbcAuthorizationRepository jdbcAuthorizationRepository = new JdbcAuthorizationRepository();
    private static final MemorySetRepository jdbcMemorySetRepository;

    static {
        try {
            jdbcMemorySetRepository = new JdbcMemorySetRepository();
        } catch (SQLException e) {
            throw new MemoryPriorityException("Failed to get connection to the database", e);
        }
    }

    private static MemorySetRepository fileMemorySetRepository = new FileMemorySetRepository();

    private static final AsyncVerseRepository asyncVerseRepository = new AsyncVerseRepositoryImpl(PORT, HOST, WEB_CLIENT, ENABLE_SSL);

    public static JdbcAuthorizationRepository getAuthorizationRepository() {
        return jdbcAuthorizationRepository;
    }

    public static MemorySetRepository getMemorySetRepository() {
        return fileMemorySetRepository;
    }

    public static AsyncVerseRepository getAsyncVerseRepository() {
        return asyncVerseRepository;
    }
}
