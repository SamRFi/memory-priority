package memorypriority.data;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;

import java.util.logging.Level;
import java.util.logging.Logger;

public class AsyncVerseRepositoryImpl implements AsyncVerseRepository {

    private static final Logger LOGGER = Logger.getLogger(AsyncVerseRepositoryImpl.class.getName());

    private final int port;
    private final String host;
    private final WebClient webClient;
    private final boolean enableSSL;

    public AsyncVerseRepositoryImpl(int port, String host, WebClient webClient, boolean enableSSL) {
        this.port = port;
        this.host = host;
        this.webClient = webClient;
        this.enableSSL = enableSSL;
    }

    public Future<String> getVerse(String verseReference) {
        String[] parts = verseReference.split("\\.");
        String baseReference = parts[0];
        int subVerse = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;

        String formattedReference = formatVerseReference(baseReference);
        String requestUrl = "/" + formattedReference + "?translation=kjv";

        return webClient.get(port, host, requestUrl)
                .ssl(enableSSL)
                .as(BodyCodec.jsonObject())
                .send()
                .map(response -> {
                    JsonObject body = response.body();
                    if (body.containsKey("error")) {
                        throw new RuntimeException(body.getString("error"));
                    }
                    String fullText = body.getString("text");
                    return subVerse > 0 ? extractSubVerse(fullText, subVerse) : fullText;
                })
                .onFailure(ex -> LOGGER.log(Level.SEVERE, "Could not load verse", ex));
    }

    private String extractSubVerse(String fullText, int subVerse) {
        String[] sentences = fullText.split("[,;.]");
        if (subVerse <= sentences.length) {
            return sentences[subVerse - 1].trim();
        } else {
            throw new RuntimeException("Subverse number exceeds the number of sentences in the verse.");
        }
    }

    private String formatVerseReference(String verseReference) {
        return verseReference.toLowerCase()
                .replace(" ", "%20")
                .replace(":", "%3A");
    }
}