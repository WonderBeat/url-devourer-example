package net.borov.integration.java.statistics;

import net.borov.statistics.InMemoryStatisticVerticle;
import org.junit.Test;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.testtools.TestVerticle;

import static org.vertx.testtools.VertxAssert.assertEquals;
import static org.vertx.testtools.VertxAssert.assertTrue;
import static org.vertx.testtools.VertxAssert.testComplete;


public class InMemoryStatisticVerticleTest extends TestVerticle {

    JsonObject config = new JsonObject("{ " +
            "\"topic\": \"url.in\", " +
            "\"statisticTopic\": \"url.stats\", " +
            "\"topListSize\": 2 }");

    @Override
    public void start() {
        initialize();
        container.deployVerticle(InMemoryStatisticVerticle.class.getName(), config, new Handler<AsyncResult<String>>() {
            @Override
            public void handle(AsyncResult<String> event) {
                assertTrue(event.succeeded());
                startTests();
            }
        });
    }

    @Test
    public void shouldCreateTopVisitedLinksStatistic() {
        JsonArray input = new JsonArray();
        input.addString("am.ru");
        input.addString("zombie.ru");
        input.addString("am.ru");
        vertx.eventBus().send("url.in", input);
        vertx.eventBus().send("url.stats", "none", new Handler<Message<JsonArray>>() {
            @Override
            public void handle(Message<JsonArray> event) {
                JsonArray data = event.body();
                assertEquals("am.ru", data.get(0));
                testComplete();
            }
        });
    }

    @Test
    public void shouldCreateOrderedRating() {
        JsonArray input = new JsonArray();
        input.addString("am.ru");
        input.addString("zombie.ru");
        input.addString("am.ru");
        input.addString("ya.ru");
        input.addString("segmento.ru");
        input.addString("google.ru");
        input.addString("borov.net");
        input.addString("borov.net");
        input.addString("borov.net");
        input.addString("borov.net");
        vertx.eventBus().send("url.in", input);
        vertx.eventBus().send("url.stats", "none", new Handler<Message<JsonArray>>() {
            @Override
            public void handle(Message<JsonArray> event) {
                JsonArray data = event.body();
                assertEquals("borov.net", data.get(0));
                assertEquals("am.ru", data.get(1));
                testComplete();
            }
        });
    }

}
