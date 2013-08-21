package net.borov;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import net.borov.processors.DomainExtractor;
import net.borov.processors.PrivateDomainExtractor;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

/**
 * URL batch processing
 */
public class UrlProcessorVerticle extends Verticle {


    private Function<String, String> urlProcessor = Functions.compose(
            new PrivateDomainExtractor(), new DomainExtractor()); // don't know how to inject this. yet.

    public void start() {
        JsonObject config = container.config();
        final String inTopic = config.getString("inputTopic");
        final String outTopic = config.getString("outputTopic");
        vertx.eventBus().registerHandler(inTopic, new Handler<Message>() {
            @Override
            public void handle(Message event) {
                JsonArray input = (JsonArray)event.body();
                JsonArray output = new JsonArray(Iterators.toArray(doProcess((Iterable) input).iterator(),
                        Object.class));
                vertx.eventBus().publish(outTopic, output);
            }
        });
    }

    private Iterable<String> doProcess(Iterable<String> items) {
        return Iterables.filter(Iterables.transform(items, urlProcessor), Predicates.<String>notNull());
    }

    public void setUrlProcessor(Function<String, String> urlProcessor) {
        this.urlProcessor = urlProcessor;
    }
}
