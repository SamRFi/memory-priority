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

    private static final ProfileRepository profileRepository = new FileProfileRepository();

    private static MemorySetRepository fileMemorySetRepository = new FileMemorySetRepository();

    private static final AsyncVerseRepository asyncVerseRepository = new AsyncVerseRepositoryImpl(PORT, HOST, WEB_CLIENT, ENABLE_SSL);

    public static ProfileRepository getProfileRepository() {
        return profileRepository;
    }

    public static MemorySetRepository getMemorySetRepository() {
        return fileMemorySetRepository;
    }

    public static AsyncVerseRepository getAsyncVerseRepository() {
        return asyncVerseRepository;
    }
}
