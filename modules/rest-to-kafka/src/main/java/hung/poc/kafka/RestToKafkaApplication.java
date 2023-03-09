package hung.poc.kafka;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;

@Slf4j
@SpringBootApplication
public class RestToKafkaApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestToKafkaApplication.class, args);
    }

}
