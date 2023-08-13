package memorypriority.data;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpResponse;
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
        String requestUrl = "/"+ verseReference + "?translation=kjv";

        return webClient.get(port, host, requestUrl)
                .ssl(enableSSL)
                .as(BodyCodec.jsonObject())
                .send()
                .map(response -> {
                    JsonObject body = response.body();
                    if (body.containsKey("error")) {
                        throw new RuntimeException(body.getString("error"));
                    }
                    return body.getString("text");
                })
                .onFailure(ex -> LOGGER.log(Level.SEVERE, "Could not load verse", ex));
    }
}
