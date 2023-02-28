For testing the SimpleWebflux
```bash
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
* [Validation with Spring Boot - Complete Guide](https://reflectoring.io/bean-validation-with-spring-boot/)
* [Java Bean Validation - Cheat Sheet](https://nullbeans.com/the-java-bean-validation-cheet-sheet/)
### 2. Testing
* [Callicoder - Spring 5 WebClient and WebTestClient Tutorial](https://www.callicoder.com/spring-5-reactive-webclient-webtestclient-examples/)