package net.borov.integration.java;

import net.borov.UrlProcessorVerticle;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.testtools.TestVerticle;

import static org.vertx.testtools.VertxAssert.assertThat;
import static org.vertx.testtools.VertxAssert.assertTrue;
import static org.vertx.testtools.VertxAssert.testComplete;


public class UrlProcessorVerticleTest extends TestVerticle {

    JsonObject config = new JsonObject("{ " +
            "\"inputTopic\": \"input.topic\", " +
            "\"outputTopic\": \"output.topic\" }");

    @Override
    public void start() {
        initialize();
        container.deployVerticle(UrlProcessorVerticle.class.getName(), config, new Handler<AsyncResult<String>>() {
            @Override
            public void handle(AsyncResult<String> event) {
                assertTrue(event.succeeded());
                startTests();
            }
        });
    }

    @Test
    public void shouldProcessInputDataAndPublishProcessed() {
        container.deployVerticle(UrlProcessorVerticle.class.getName(), config);
        vertx.eventBus().registerHandler("output.topic", new Handler<Message>() {
            @Override
            public void handle(Message event) {
                JsonArray array = (JsonArray)event.body();
                assertThat((String)array.get(0), CoreMatchers.is("segmento.ru"));
                testComplete();
            }
        });
        JsonArray inputArr = new JsonArray();
        inputArr.add("http://www.segmento.ru/");
        vertx.eventBus().publish("input.topic", inputArr);
    }
}
