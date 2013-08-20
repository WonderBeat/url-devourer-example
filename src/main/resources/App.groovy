import org.vertx.groovy.core.eventbus.EventBus

def config = [
    server: [
        bufferSize: 100,
        host: 'localhost',
        port: 8080,
        dataExportTopic: 'url.process.raw',
        flushTimeout: 2 // seconds
    ],

    urlProcessor: [
        inputTopic: 'url.process.raw',
        outputTopic: 'url.process.done',
    ],

    topListAggregator: [
        topic: 'url.process.done',
        cacheSize: 1000 // items
    ]
]

container.deployVerticle('net.borov.DevourerServer', config.server, 1)
