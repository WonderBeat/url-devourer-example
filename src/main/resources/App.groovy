import org.vertx.groovy.core.eventbus.EventBus

def config = [
    useRedis: false,
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
    ],

    redisTopListAggregator: [
        address: 'url.statistics.redis',
        topic: 'url.process.done',
        statisticTopic: 'url.statistics',
        topListSize: 10,
        workerNode: false,
        instances: 2,
        redisGus: 'redis.bus',
        redisKey: 'rating'
    ],

    redisBus: [
        address: 'redis.bus',
        host: '127.0.0.1',
        port: 6379
    ]
]

container.deployVerticle('net.borov.DevourerServer', config.server, config.server.instances)
container.deployVerticle('net.borov.UrlProcessorVerticle', config.urlProcessor, config.urlProcessor.instances)

if(config.useRedis) {
    // Based on https://github.com/pmlopes/mod-redis-io
    container.deployVerticle('net.borov.statistics.RedisBasedStatistic', config.redisTopListAggregator,
            config.topListAggregator.instances)
    container.deployModule("com.jetdrone.mod-redis-io-1.1", config.redisBus, 1)
} else {
    // In memory statistic is not appropriate for production environment ;)
    container.deployVerticle('net.borov.statistics.InMemoryStatisticVerticle', config.topListAggregator,
            config.topListAggregator.instances)
}


