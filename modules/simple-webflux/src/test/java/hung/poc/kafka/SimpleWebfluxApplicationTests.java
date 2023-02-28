package hung.poc.kafka;

import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@WebFluxTest
class SimpleWebfluxApplicationTests {

    @Autowired
    private WebTestClient webClient;

    @Test
    @WithMockUser(roles = {"user"})
    void requestMappingTest() {
        SimpleWebfluxApplication.Customer customer = new SimpleWebfluxApplication.Customer(UUID.randomUUID(),"john.ng@gmail.com");
        customer.setFirstName("John");
        customer.setLastName("Doe");

        webClient.post().uri("/api/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(customer), SimpleWebfluxApplication.Customer.class)
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    @WithMockUser(roles = {"user"})
    void emailInBackListTest() {
        SimpleWebfluxApplication.Customer customer1 = new SimpleWebfluxApplication.Customer(UUID.randomUUID(),"john.doe@gmail.com");
        customer1.setFirstName("John");
        customer1.setLastName("Doe");

        webClient.post().uri("/api/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(customer1), SimpleWebfluxApplication.Customer.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$[0].codes").isArray()
                .jsonPath("$[0].codes.[1]").isEqualTo("in-blacklist.email")
                .jsonPath("$[0].codes").value(array -> {
                    Assert.isTrue(CollectionUtils.contains(array.iterator(),"in-blacklist.email"),"The blacklist message code is missing in response");
                }, JSONArray.class);
    }

    @Test
    @WithMockUser(roles = {"user"})
    void javaBeanValidationTest() {
        //HttpWebHandlerAdapter
                //ExchangeFunctions
        SimpleWebfluxApplication.Customer customer1 = new SimpleWebfluxApplication.Customer(UUID.randomUUID(),"Invalid Email Address");
        customer1.setFirstName("This is a very very very loooooooooooooooooooooong first name");
        customer1.setLastName("This is a very very very loooooooooooooooooooooong last name");

        webClient.post().uri("/api/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(customer1), SimpleWebfluxApplication.Customer.class)
                .exchange()
                .expectStatus().isBadRequest();
//                .expectBody()
//                .jsonPath("$[0].codes").value(array -> {
//                    Assert.isTrue(CollectionUtils.contains(array.iterator(),"Email.customer.email"),"email should be found as invalid");
//                    //Assert.isTrue(CollectionUtils.contains(array.iterator(),"Size.customer.firstName"),"firstName should be found as invalid");
//                    //Assert.isTrue(CollectionUtils.contains(array.iterator(),"Size.customer.lastName"),"lastName should be found as invalid");
//                }, JSONArray.class);
    }
}
