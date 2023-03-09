package hung.poc.kafka.integration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.kafka.dsl.Kafka;
import org.springframework.integration.webflux.dsl.WebFlux;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.messaging.MessageChannel;

@Slf4j
@Configuration
public class PolygonIOFlow {

    @Bean
    public IntegrationFlow kafkaOutboundFlow() {
        return IntegrationFlow
                .from(WebFlux.inboundGateway("/api/ticker")
                        .requestMapping(mapping -> mapping.methods(HttpMethod.POST).consumes(MediaType.TEXT_PLAIN_VALUE))
                        .requestPayloadType(String.class)
                        .replyChannel("kafka-out")
                        .replyTimeout(2000)
                        .errorChannel("kafka-error"))
                .log()
                .handle(Kafka.outboundGateway(replyTemplate(null)))
                .log()
                .channel("kafka-out")
                .get();
    }

    @Bean(name = "kafka-error")
    public MessageChannel kafkaErrorChannel() {
        return MessageChannels.direct("kafka-error").get();
    }

    @Bean
    public ReplyingKafkaTemplate<String,String,String> replyTemplate(
            ProducerFactory<String,String> factory
    ) {
        ReplyingKafkaTemplate replyTemplate = new ReplyingKafkaTemplate<>(factory,replyContainer(null));
        replyTemplate.setDefaultTopic("ticker-input");
        return replyTemplate;
    }

    @Bean
    public KafkaMessageListenerContainer<String,String> replyContainer(
            ConsumerFactory<String,String> factory
    ) {
        ContainerProperties props = new ContainerProperties("ticker-output");
        props.setGroupId("test-group");
        KafkaMessageListenerContainer container = new KafkaMessageListenerContainer<>(factory,props);
        return container;
    }
}
