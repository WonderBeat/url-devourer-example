import org.vertx.groovy.core.eventbus.EventBus

def config = [
    server: [
        bufferSize: 100,
        host: 'localhost',
        port: 8080,
        dataExportTopic: 'url.process.raw',
        flushTimeout: 2, // seconds
        instances: 2
    ],

    urlProcessor: [
        inputTopic: 'url.process.raw',
        outputTopic: 'url.process.done',
        instances: 2
    ],

    topListAggregator: [
        topic: 'url.process.done',
        cacheSize: 1000, // items
        workerNode: true
    ]
]

container.deployVerticle('net.borov.DevourerServer', config.server, config.server.instances)
container.deployVerticle('net.borov.UrlProcessorVerticle', config.urlProcessor, config.urlProcessor.instances)
