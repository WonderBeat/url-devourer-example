package net.borov;

import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;


public class DevourerServer extends Verticle {
    /**
     * Verticle can't be shared across threads. There is no need to synchronize
     */
    JsonArray queryBuffer;

    String dataExportTopic;

    public void start() {
        JsonObject config = container.config();

        final int bufferSize = config.getInteger("bufferSize");
        int port = config.getInteger("port");
        String host = config.getString("host");
        setFlushTimer(config.getInteger("flushTimeout"));
        dataExportTopic = config.getString("dataExportTopic");

        RouteMatcher routeMatcher = new RouteMatcher();
        initGrabberVerticle(bufferSize, routeMatcher);

        final String statisticTopic = config.getString("statisticTopic");
        initStatisticsHandler(statisticTopic, routeMatcher);

        final HttpServer server = vertx.createHttpServer();
        server.requestHandler(routeMatcher);
        server.listen(port, host);
    }

    private void initStatisticsHandler(final String statisticTopic, RouteMatcher routeMatcher) {
        routeMatcher.get("/top", new Handler<HttpServerRequest>() {
            @Override
            public void handle(final HttpServerRequest request) {
                vertx.eventBus().send(statisticTopic, "", new Handler<Message<JsonArray>>() {
                    @Override
                    public void handle(Message<JsonArray> statistics) {
                        request.response().end(statistics.body().encode());
                    }
                });
            }
        });
    }

    private void initGrabberVerticle(final int bufferSize, RouteMatcher routeMatcher) {

        queryBuffer = new JsonArray();
        routeMatcher.post("/add", new Handler<HttpServerRequest>() {
            public void handle(HttpServerRequest request) {
                request.dataHandler(new Handler<Buffer>() {
                    public void handle(Buffer buffer) {
                        queryBuffer.add(buffer.toString());
                        if (queryBuffer.size() >= bufferSize) {
                            flush();
                        }
                    }
                });
                request.response().end();
            }
        });
    }

    private long setFlushTimer(int timeout) {
        return vertx.setPeriodic(timeout, new Handler<Long>() {
            @Override
            public void handle(Long event) {
                if (queryBuffer.size() > 0) {
                    flush();
                }
            }
        });
    }

    private void flush() {
        vertx.eventBus().send(dataExportTopic, queryBuffer);
        queryBuffer = new JsonArray();
    }
}
