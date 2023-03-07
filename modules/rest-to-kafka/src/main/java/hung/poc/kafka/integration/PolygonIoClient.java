package hung.poc.kafka.integration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.kafka.dsl.Kafka;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.integration.webflux.dsl.WebFlux;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.ConsumerProperties;
import org.springframework.messaging.Message;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@Slf4j
//@Configuration
public class PolygonIoClient {

    @Bean
    public IntegrationFlow getTickers() {
        WebClient webclient = WebClient.builder()
                //.baseUrl("https://api.polygon.io/v3")
                .defaultHeader(HttpHeaders.AUTHORIZATION,"Bearer yf2pbT2Sl_EtfbhUkFcad33fg27Z3goH")
                .build();

        return IntegrationFlow
                 .from(WebFlux.inboundChannelAdapter("/api/ticker")
                         .requestMapping(mapping -> mapping.methods(HttpMethod.GET))
                         .requestPayloadType(String.class))
                 .log()
                 .transform(Message.class, msg -> {
                     return MessageBuilder.fromMessage(msg).removeHeaders("Authorization","Host").build();
                 })
                 .log()
                 .handle(WebFlux.outboundGateway(m ->
                               UriComponentsBuilder
                                  .fromUriString("https://api.polygon.io/v3/reference/tickers")
                                  .queryParam("ticker","AAPL")
                                  //.queryParam("apiKey","yf2pbT2Sl_EtfbhUkFcad33fg27Z3goH")
                                  .build()
                                  .toUri(), webclient)
                           .httpMethod(HttpMethod.GET)
                           .expectedResponseType(String.class))
                 .handle(s -> {
                     log.info("{}",s);
                 })
                 .get();
    }

    @Bean
    public IntegrationFlow abc(ConsumerFactory<UUID,String> factory) {
        ConsumerProperties props = new ConsumerProperties("");
        return IntegrationFlow.from(Kafka.inboundChannelAdapter(factory,props)).get();
    }
}
