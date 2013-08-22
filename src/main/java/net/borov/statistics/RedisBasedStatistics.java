package net.borov.statistics;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

/**
 * Redis based statistics.
 *
 * Data type: Sorted Set
 * http://redis.io/topics/data-types
 *
 * @author Denis Golovachev
 * @author $Author$ (current maintainer)
 * @since 1.0
 */
public class RedisBasedStatistics extends Verticle {

    @Override
    public void start() {
        JsonObject config = container.config();
        String inputTopic = config.getString("topic");
        String redisAddress = config.getString("redisBus");
        String redisKey = config.getString("redisKey");
        initStatisticAggregator(inputTopic, redisAddress, redisKey);

        String statisticTopic = config.getString("statisticTopic");
        final int topListSize = config.getInteger("topListSize");
        initStatisticPublisher(statisticTopic, topListSize, redisKey, redisAddress);
    }

    private void initStatisticAggregator(String inputTopic, final String redisAddress, final String redisKey) {
        vertx.eventBus().registerHandler(inputTopic, new Handler<Message>() {
            final String redisInsertQuery = "{command: \"ZINCRBY\", key: \"" + redisKey + " \", increment: 1, member: \"%1\" }";
            @Override
            public void handle(Message event) {
                JsonArray array = (JsonArray)event.body();
                for(Object element: array) {
                    String domain = (String)element;
                    requestData(redisAddress, new JsonObject(String.format(redisInsertQuery, domain)));
                }
            }
        });
    }

    private void initStatisticPublisher(String statisticTopic, final int topListSize, String redisKey, final String redisAddress) {
        final String getStatsQuery = "{ command: \"zrange\", key: " + redisKey + ", start: 0, stop: " + topListSize + " }";
        vertx.eventBus().registerHandler(statisticTopic, new Handler<Message>() {
            @Override
            public void handle(final Message request) {
                requestData(redisAddress, getStatsQuery, new Handler<Message<JsonArray>>() {
                    @Override
                    public void handle(Message<JsonArray> redisStatsData) {
                        request.reply(redisStatsData);
                    }
                });
            }
        });
    }

    private <T> void requestData(String redisAddress, String query, Handler<Message<T>> handler) {
        vertx.eventBus().send(redisAddress, query, handler);
    }

    private <T> void requestData(String redisAddress, Object query) {
        vertx.eventBus().send(redisAddress, query);
    }
}
