package net.borov.statistics;

import com.google.common.base.Functions;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

import java.util.List;
import java.util.SortedMap;

/**
 * In memory data statistics.
 * Can be used in DEV mode
 *
 * @author Denis Golovachev
 * @author $Author$ (current maintainer)
 * @since 1.0
 */
public class InMemoryStatisticVerticle extends Verticle {

    /**
     * No need to synchronize it according VertX documentation
     */
    SortedMap<String, Long> ratedElements = Maps.newTreeMap();

    Ordering<String> naturalOrdering = Ordering.natural().onResultOf(Functions.forMap(ratedElements));

    @Override
    public void start() {
        JsonObject config = container.config();
        String inputTopic = config.getString("topic");
        initStatisticAggregator(inputTopic);

        String statisticTopic = config.getString("statisticTopic");
        final int topListSize = config.getInteger("topListSize");
        initStatisticPublisher(statisticTopic, topListSize);
    }

    private void initStatisticAggregator(String inputTopic) {
        vertx.eventBus().registerHandler(inputTopic, new Handler<Message>() {
            @Override
            public void handle(Message event) {
                JsonArray array = (JsonArray)event.body();
                for(Object element: array) {
                    String domain = (String)element;
                    Long currentRating = ratedElements.get(domain);
                    Long updatedRating = currentRating == null ? 1 : ++currentRating;
                    ratedElements.put(domain, updatedRating);
                }
            }
        });
    }

    private void initStatisticPublisher(String statisticTopic, final int topListSize) {
        vertx.eventBus().registerHandler(statisticTopic, new Handler<Message>() {
            @Override
            public void handle(Message event) {
                List<String> top = naturalOrdering.greatestOf(ratedElements.keySet(), topListSize);
                JsonArray json = new JsonArray(top.toArray());
                event.reply(json);
            }
        });
    }
}
