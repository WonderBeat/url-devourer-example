url-devourer-example
====================

URL grabber. Just an example of VertX based Async network application.

Application is a network server.
Design goal is performance and scalability.  
Just Proof of Concept, how distributed async application can be based on VertX.

## Compile and Run
- mvn compile
- mvn vertx:runMod

##Usage
Server accepts POST requests with URL payload.

```js
POST 127.0.0.1:8080/add/
Content-Length: 200

http://url-to-track
```
URLs are used to perform statistical analysis.  
Top tracked URLs are available here:

```js
GET http://localhost:8080/top
```

##Synthetic Testing
JMeter  
10 threads, 1000 requests each.
![JMeter results](https://raw.github.com/WonderBeat/url-devourer-example/master/throughput-jmeter-in-memory-store.png)
about 4k requests per second

##Results
- VertX doesn't provide any IOC functionality. It'll be difficult to maintain complex project. Embeded VerteX can be a solution.  
- Polyglot background forces us to use JSON messages on event bus. May be overhead.
