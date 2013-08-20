package net.borov;

import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
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
        initServerVerticle(bufferSize, port, host);
    }

    private void initServerVerticle(final int bufferSize, int port, String host) {
        final HttpServer server = vertx.createHttpServer();
        queryBuffer = new JsonArray();
        server.requestHandler(new Handler<HttpServerRequest>() {
            public void handle(HttpServerRequest request) {
                request.dataHandler(new Handler<Buffer>() {
                    public void handle(Buffer buffer) {
                        queryBuffer.add(buffer.toString());
                        if(queryBuffer.size() >= bufferSize) {
                            flush();
                        }
                    }
                });
                request.response().end();
            }
        }).listen(port, host);
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
        vertx.eventBus().publish(dataExportTopic, queryBuffer);
        queryBuffer = new JsonArray();
    }
}
