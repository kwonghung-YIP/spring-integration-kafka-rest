package hung.poc.kafka;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Slf4j
@SpringBootApplication
public class SimpleWebfluxApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimpleWebfluxApplication.class, args);
    }

    @Configuration
    @EnableWebFluxSecurity
    static public class WebfluxSecConfig {

        @Bean
        public SecurityWebFilterChain apiSecConfig(ServerHttpSecurity http) {
            return http.csrf(csrf -> csrf.disable())
                    .authorizeExchange(ex ->
                            ex.pathMatchers(HttpMethod.GET,"/actuator/**").permitAll()
                               .pathMatchers(HttpMethod.POST,"/api/customer").hasRole("user")
                               .anyExchange().authenticated())
                    .httpBasic().and()
                    .build();
        }
    }

    @RestController
    @RequestMapping(path = "/api")
    static public class SimpleRestController {

        @InitBinder
        void initBinder(WebDataBinder binder) {
            binder.addValidators(new CustomerValidator());
        }

        @ExceptionHandler(WebExchangeBindException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public Mono<List<ObjectError>> handleWebExchangeBindException(WebExchangeBindException e) {
            log.error("",e.getAllErrors());
            return Mono.just(e.getAllErrors());
        }

        @PostMapping(path="/customer",consumes = MediaType.APPLICATION_JSON_VALUE)
        public Mono<ResponseEntity<Void>> placeOrder(
            @Valid @RequestBody Customer customer,
            Principal principal
        ) throws URISyntaxException {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Customer newCustomer = new Customer(UUID.randomUUID(), customer.getEmail());
            BeanUtils.copyProperties(customer,newCustomer,"id","email");
            newCustomer.setCreatedBy(principal.getName());
            return Mono.just(ResponseEntity.created(new URI("/customer/"+newCustomer.getId())).build());
        }
    }

    @Data
    @RequiredArgsConstructor
    static public class Customer {
        @NotNull
        final private UUID id;

        @NotNull
        @Size(max = 30)
        private String firstName;

        @NotNull
        @Size(max = 30)
        private String lastName;
        @Email
        final private String email;

        private String createdBy;
    }

    static public class CustomerValidator implements Validator {

        @Override
        public boolean supports(Class<?> clazz) {
            return Customer.class.isAssignableFrom(clazz);
        }

        @Override
        public void validate(Object target, Errors errors) {
            List<String> blacklist = List.of("john.doe@gmail.com");
            log.info("Validate customer object...");
            Customer customer = (Customer)target;
            if (blacklist.contains(customer.getEmail())) {
                errors.rejectValue("email","in-blacklist","Email in blacklist");
            }
        }
    }
}
