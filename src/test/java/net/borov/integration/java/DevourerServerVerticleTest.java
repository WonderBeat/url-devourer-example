package net.borov.integration.java;

import net.borov.DevourerServer;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpClientResponse;
import org.vertx.java.core.json.JsonObject;
import org.vertx.testtools.TestVerticle;

import static org.vertx.testtools.VertxAssert.*;

/**
 * If you see it, than I've forgotten javadoc
 *
 * @author Denis Golovachev
 * @author $Author$ (current maintainer)
 * @since 1.0
 */
public class DevourerServerVerticleTest extends TestVerticle {

    JsonObject config = new JsonObject("{ " +
            "\"bufferSize\": 2, " +
            "\"host\": \"localhost\", " +
            "\"port\": 8080, " +
            "\"dataExportTopic\": \"url.process.raw\", " +
            "\"flushTimeout\": 2}");

    //HttpClient httpClient = vertx.createHttpClient().setPort(8080);

    @Test
    public void shouldFlushOnTimeout() {
        container.deployVerticle(DevourerServer.class.getName(), config);
        performRequest();
        vertx.eventBus().registerHandler("url.process.raw", new Handler<Message>() {
            @Override
            public void handle(Message event) {
                assertThat(event, CoreMatchers.notNullValue());
                testComplete();
            }
        });
    }

    @Test
    public void shouldFlushOnBufferOverflow() {
        container.deployVerticle(DevourerServer.class.getName(), config);
        performRequest();
        performRequest(); // Reaches buffer size
        vertx.eventBus().registerHandler("url.process.raw", new Handler<Message>() {
            @Override
            public void handle(Message event) {
                assertThat(event, CoreMatchers.notNullValue());
                testComplete();
            }
        });
    }

    private void performRequest() {
        vertx.createHttpClient().setPort(8080).post("/someurl", new Handler<HttpClientResponse>() {
            @Override
            public void handle(HttpClientResponse resp) {
                assertEquals(200, resp.statusCode());
            }
        }).putHeader("Content-Length", "1").write("url").end();
    }

}

