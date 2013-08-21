import org.vertx.groovy.core.eventbus.EventBus

def config = [
    server: [
        bufferSize: 100,
        host: 'localhost',
        port: 8080,
        dataExportTopic: 'url.process.raw',
        statisticTopic: 'url.statistics',
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
        statisticTopic: 'url.statistics',
        cacheSize: 1000, // items
        topListSize: 10,
        workerNode: true,
        instances: 1
    ]
]

container.deployVerticle('net.borov.DevourerServer', config.server, config.server.instances)
container.deployVerticle('net.borov.UrlProcessorVerticle', config.urlProcessor, config.urlProcessor.instances)

// In memory statistic is not appropriate for production environment ;)
container.deployVerticle('net.borov.statistics.InMemoryStatisticVerticle', config.topListAggregator,
        config.topListAggregator.instances)
