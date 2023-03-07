package hung.poc.kafka.integration;

import hung.poc.kafka.pojo.SingleResult;
import hung.poc.kafka.pojo.TickerInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.dsl.Transformers;
import org.springframework.integration.kafka.dsl.Kafka;
import org.springframework.integration.kafka.inbound.KafkaMessageDrivenChannelAdapter;
import org.springframework.integration.webflux.dsl.WebFlux;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.messaging.MessageChannel;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@Slf4j
@Configuration
public class PolygonIOFlow {

    @Value("${polygon.apikey}")
    private String apiKey;

    @Bean
    public IntegrationFlow kafkaInboundFlow(
            ConsumerFactory<UUID, String> consumerFactory,
            ProducerFactory<UUID, String> producerFactory
    ) {
        return IntegrationFlow.from(
                        Kafka.messageDrivenChannelAdapter(
                                        consumerFactory, KafkaMessageDrivenChannelAdapter.ListenerMode.record, "ticker-input"
                                )
                                .configureListenerContainer(c -> c.groupId("ticker-group"))
                                .payloadType(String.class)
                )
                .log()
                .handle("polygonRestGateway", "getTickerInfo")
                .transform(Transformers.<SingleResult<TickerInfo>, TickerInfo>converter(result -> {
                    return result.getResults();
                }))
                .log()
                .handle(
                        Kafka.outboundChannelAdapter(producerFactory).topic("ticker-output")
                )
                .get();
    }

    @MessagingGateway(name = "polygonRestGateway")
    public interface PolygonRestGateway {

        @Gateway(requestChannel = "polygon-gw-ticker-info-request", replyChannel = "polygon-gw-ticker-info-reply", replyTimeout = 2000)
        public SingleResult<TickerInfo> getTickerInfo(String ticker);
    }

    @Bean(name = "polygon-gw-ticker-info-request")
    public MessageChannel getTickerInfoRequest() {
        return MessageChannels.direct("polygon-gw-ticker-info-request").get();
    }

    @Bean(name = "polygon-gw-ticker-info-reply")
    public MessageChannel getTickerInfoReply() {
        return MessageChannels.direct("polygon-gw-ticker-info-reply").get();
    }

    @Bean
    public IntegrationFlow getTickerInfoFlow() {
        WebClient webclient = WebClient.builder()
                //.baseUrl("https://api.polygon.io")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .build();

        // Polygon REST API Reference
        // https://polygon.io/docs/stocks/get_v3_reference_tickers__ticker
        return IntegrationFlow.from(getTickerInfoRequest())
                .handle(WebFlux.<String>outboundGateway(m ->
                                UriComponentsBuilder
                                        .fromPath("/v3/reference/tickers/{ticker}")
                                        .host("api.polygon.io")
                                        .scheme("https")
                                        //.queryParam("ticker","AAPL")
                                        .build(m.getPayload().toUpperCase()), webclient)
                        .httpMethod(HttpMethod.GET)
                        .expectedResponseType(new ParameterizedTypeReference<SingleResult<TickerInfo>>() {
                        }))
                .channel(getTickerInfoReply())
                .get();
    }

}
