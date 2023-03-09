package hung.poc.kafka.integration;

import hung.poc.kafka.pojo.Customer;
import hung.poc.kafka.pojo.CustomerValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.webflux.dsl.WebFlux;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Slf4j
@Configuration
public class InboundAdapterConfig {

    @Bean
    public IntegrationFlow createCustomerFlow() {
        return IntegrationFlow.from(
                        WebFlux.inboundChannelAdapter("/api/customer")
                                .requestMapping(mapping -> mapping.methods(HttpMethod.POST).consumes(MediaType.APPLICATION_JSON_VALUE))
                                .requestPayloadType(Customer.class)
                                .validator(new CustomerValidator(beanValidator()))
                                .statusCodeFunction(respEntity -> HttpStatus.CREATED)
                )
                .transform(Message.class, msg -> {
                    MessageHeaders headers = msg.getHeaders();
                    Authentication auth = headers.get("http_userPrincipal", Authentication.class);
                    Customer payload = (Customer) msg.getPayload();
                    payload.setCreatedBy(auth.getName());
                    return MessageBuilder.withPayload(payload).build();
                })
                .channel("to-kafka")
                .log()
                .get();
    }

    @ServiceActivator(inputChannel = "to-kafka")
    public void viewCustomer(Message<Customer> msg) {
        log.info("Customer: {}", msg);
    }

    @Bean
    public LocalValidatorFactoryBean beanValidator() {
        return new LocalValidatorFactoryBean();
    }
}
