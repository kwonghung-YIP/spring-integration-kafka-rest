package hung.poc.kafka;

import hung.poc.kafka.pojo.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

@SpringBootTest
@AutoConfigureWebTestClient
//@WebFluxTest
//@SpringIntegrationTest
//@Import({WebFluxSecConfig.class, InboundAdapterConfig.class})
class RestToKafkaApplicationTests {
    @Autowired
    private WebTestClient webClient;

    @Test
    @WithMockUser(username="peter", roles = {"user"})
    void requestMappingTest() {
        Customer customer = new Customer();
        customer.setId(UUID.randomUUID());
        customer.setEmail("johnny.ng@gmail.com");
        customer.setFirstName("John");
        customer.setLastName("Doe");

        webClient.post().uri("/api/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(customer), Customer.class)
                .exchange()
                .expectStatus().isCreated();
    }

}
