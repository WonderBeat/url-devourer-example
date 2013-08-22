url-devourer-example
====================

URL grabber. 
Proof of Concept, how distributed async application can be based on VertX.

Application is a network server.
Design goal is performance and scalability.  

##Usage
Server accepts POST requests with URL payload.
```js
POST 127.0.0.1:8080/add/
Content-Length: 200

http://url-to-track
```
URLs are used to perform statistical analysis.  
Top tracked **private domains** are available here:
```js
GET http://localhost:8080/top
```

## Compile and Run
`mvn compile vertx:runMod`

##Synthetic Testing
**Tool:** JMeter  
**Suite:** 10 threads, 1000 requests each.  
**Other:** Application is configured with in-memory storage.  
![JMeter results](https://raw.github.com/WonderBeat/url-devourer-example/master/throughput-jmeter-in-memory-store.png)
about 4k requests per second

##Results
- VertX doesn't provide any IOC functionality. It'll be difficult to maintain complex project. Embeded VerteX can be a solution.  
- Polyglot background forces us to use JSON messages on event bus. Can be overhead.
- Java is not a language I want to use with VertX. Scripting languages suits better.
