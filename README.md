```bash
curl -v -X POST \
  --compressed --http2 \
  --user john:passwd \
  --header "Content-Type: text/plain" \
  --data 'peter pan' \
  localhost:8080/api/uppercase

curl -v -X POST \
  --compressed --http2 \
  --user john:passwd \
  --header "Content-Type: text/plain" \
  --data 'aapl' \
  localhost:8080/api/ticker
```

```bash
docker compose \
  -f kafka-cluster.yaml up -d
```

For testing the SimpleWebflux

```bash
curl -v -X POST \
  --compressed --http2 \
  --user john:passwd \
  --header "Content-Type: application/json" \
  --data '{"id":"d155a1e0-b6ab-11ed-afa1-0242ac120002","firstName":"Johnny","lastName":"Doe","email":"johnny.doe@gmail.com"}' \
  localhost:8080/api/customer
  
curl -v -X POST \
  --compressed --http2 \
  --user john:passwd \
  --header "Content-Type: application/json" \
  --data '{"id":"d155a1e0-b6ab-11ed-afa1-0242ac120002","firstName":"John","lastName":"Doe","email":"john.doe@gmail.com"}' \
  localhost:8080/api/customer

curl -v -X POST \
  --compressed --http2 \
  --user john:passwd \
  --header "Content-Type: application/json" \
  --data '{"id":"d155a1e0-b6ab-11ed-afa1-0242ac120002","firstName":"John","lastName":"Doe","email":"Invalid Email Address"}' \
  localhost:8080/api/customer
```
```bash
curl -v -X GET \
  localhost:8081/actuator/health
```

## Reference

### 1. Validation
* [Spring Framework Core - Spring Validation Interface](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#validator)
* [Spring Framework Core - Java Bean Validation (JSR-303)](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#validation-beanvalidation)
* [Reflectoring.io - Validation with Spring Boot - Complete Guide](https://reflectoring.io/bean-validation-with-spring-boot/)
* [Java Bean Validation - Cheat Sheet](https://nullbeans.com/the-java-bean-validation-cheet-sheet/)

### 2. Testing
* [Callicoder - Spring 5 WebClient and WebTestClient Tutorial](https://www.callicoder.com/spring-5-reactive-webclient-webtestclient-examples/)

### 3. Spring Boot Testing
* [Reflectoring.io - Testing with Spring Boot and @SpringBootTest](https://reflectoring.io/spring-boot-test/)

### 4. Jmeter 
* [Jmeter Plugin Manager](https://jmeter-plugins.org/wiki/PluginsManager/)
* [Jmeter Generate Random Value](https://hkrtrainings.com/jmeter-random-string#Jmeter)
* [Jmeter variable to lowercase](https://stackoverflow.com/questions/4755286/in-jmeter-and-beanshell-how-can-i-make-a-variable-lowercase)

### 5. FasterXML Jackson2 JSON Annotations
* [Project GitHub - Jackson Annotation](https://github.com/FasterXML/jackson-annotations/wiki/Jackson-Annotations)
* [Tutorialspoint - Jackson Annotation](https://www.tutorialspoint.com/jackson_annotations/jackson_annotations_jsonsetter.htm)

### 6. Enterprise Integration Patterns
* [Enterprise Integration Patterns](https://www.enterpriseintegrationpatterns.com/patterns/messaging/)

### Utilities Library
* [MapStruct - Java Bean Mapper](https://mapstruct.org/)
* [Redoc - Generate OpenAPI Doc](https://github.com/Redocly/redoc)