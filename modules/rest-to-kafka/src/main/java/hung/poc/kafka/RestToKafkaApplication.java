package hung.poc.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@SpringBootApplication
public class RestToKafkaApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestToKafkaApplication.class, args);
    }

    //@Bean
    public ApplicationRunner simpleWebClient() {
        return (argv) -> {
            WebClient webclient = WebClient.builder()
                    .baseUrl("https://api.polygon.io")
                    .defaultHeader(HttpHeaders.AUTHORIZATION,"Bearer yf2pbT2Sl_EtfbhUkFcad33fg27Z3goH")
                    .build();

            webclient.get().uri(uriBuilder ->
                            uriBuilder.path("/v3/reference/tickers")
                                    .queryParam("ticker","AAPL")
                                    .build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .log()
                    .subscribe(s -> {log.info("{}",s);});
        };
    }
}
