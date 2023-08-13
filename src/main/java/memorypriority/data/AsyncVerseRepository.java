package memorypriority.data;

import io.vertx.core.Future;

public interface AsyncVerseRepository {
    Future<String> getVerse(String verseReference);
}
