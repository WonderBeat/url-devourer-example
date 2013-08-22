package net.borov.integration.java;

import net.borov.DevourerServer;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpClientResponse;
import org.vertx.java.core.json.JsonObject;
import org.vertx.testtools.TestVerticle;

import static org.vertx.testtools.VertxAssert.*;

public class DevourerServerVerticleTest extends TestVerticle {

    JsonObject config = new JsonObject("{ " +
            "\"bufferSize\": 2, " +
            "\"host\": \"localhost\", " +
            "\"port\": 8080, " +
            "\"dataExportTopic\": \"url.process.raw\", " +
            "\"statisticTopic\": \"stat\", " +
            "\"flushTimeout\": 2}");

    @Override
    public void start() {
        initialize();
        container.deployVerticle(DevourerServer.class.getName(), config, new Handler<AsyncResult<String>>() {
            @Override
            public void handle(AsyncResult<String> event) {
                assertTrue(event.succeeded());
                startTests();
            }
        });
    }

    @Test
    public void shouldFlushOnBufferOverflow() {
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

    @Test
    public void shouldFlushOnTimeout() {
        performRequest();
        vertx.eventBus().registerHandler("url.process.raw", new Handler<Message>() {
            @Override
            public void handle(Message event) {
                assertThat(event, CoreMatchers.notNullValue());
                testComplete();
            }
        });
    }

    private void performRequest() {
        vertx.createHttpClient().setPort(8080).post("/add", new Handler<HttpClientResponse>() {
            @Override
            public void handle(HttpClientResponse resp) {
                assertEquals(200, resp.statusCode());
            }
        }).putHeader("Content-Length", "1").write("url").end();
    }

}

