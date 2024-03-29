package hung.poc.spring.integration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.handler.advice.RequestHandlerCircuitBreakerAdvice;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Slf4j
@Configuration
//@Profile("example1")
public class GatewayErrorHandlingConfig {

    //@Bean
    public ApplicationRunner startFlow(SimpleGateway gateway) {
        return (argv) -> {
            for (int i=0;i<10;i++) {
                try {
                    Optional<TickerInfo> info = gateway.getTickerInfo("AAPL");
                    log.info("Ticker Name: {}", info);
                } catch (Exception e) {
                    log.error("caught exception", e);
                }
            }
        };
    }
    @Bean
    public IntegrationFlow firstFlow() {
        return IntegrationFlow
                .fromSupplier(() -> "AAPL", s -> s.poller(Pollers.fixedDelay(Duration.ofMillis(500))))
                .log()
                .handle("simpleGateway","getTickerInfo")
                .log()
                .handle(m -> {
                    Optional<TickerInfo> payload = (Optional<TickerInfo>)m.getPayload();
                    log.info("Print name: {}",payload.map(info -> info.getName()).orElse("Ticker Not Found"));
                })
                .get();
    }

    @Bean(name="gw-reply-channel")
    public MessageChannel replyChannel(@Qualifier("loggingFlow") IntegrationFlow loggingFlow) {
        return MessageChannels.publishSubscribe("gw-reply-channel")
                //.wireTap(loggingFlow.getInputChannel())
                .get();
    }

    @Bean
    public IntegrationFlow loggingFlow() {
        return f -> f.log();
    }

    @MessagingGateway(name = "simpleGateway", errorChannel = "gw-error-channel")
    static public interface SimpleGateway {

        @Gateway(requestChannel = "gw-request-channel", replyChannel = "gw-reply-channel", replyTimeout = 2000)
        public Optional<TickerInfo> getTickerInfo(String ticker) throws TickerNotFoundException;

    }

    @Bean
    public IntegrationFlow gatewayErrorFlow() {
        return IntegrationFlow.from("gw-error-channel")
                .routeByException(r -> r
                        .subFlowMapping(TickerNotFoundException.class,f -> f.transform(e -> Optional.empty()))
                        .defaultOutputToParentFlow())
                .get();
    }

    @Service
    static public class TickerService {

        @ServiceActivator(inputChannel = "gw-request-channel", outputChannel = "gw-reply-channel", adviceChain = {"circuitBreakerAdvice"})
        public Optional<TickerInfo> getTickerInfo(String ticker) throws TickerNotFoundException {
            if (Math.random()>0.5) {
                throw new TickerNotFoundException(ticker);
            } else {
                return Optional.of(new TickerInfo(ticker,"Apple Inc."));
            }
        }

        @ServiceActivator(inputChannel = "gw-reply-channel")
        public void anotherHandler(Optional<TickerInfo> info) {
            log.info("Another handler {}",info);
        }

//        @ServiceActivator(inputChannel = "gw-error-channel")
//        public String handleException(MessageHandlingException e) {
//            log.error("Error logged from error channel",e);
//            if (e.getCause() instanceof TickerNotFoundException) {
//                return "Name Not Found";
//            } else {
//                throw e;
//            }
//        }
    }

    @Bean
    public RequestHandlerCircuitBreakerAdvice circuitBreakerAdvice() {
        RequestHandlerCircuitBreakerAdvice advice = new RequestHandlerCircuitBreakerAdvice();
        advice.setThreshold(3);
        advice.setHalfOpenAfter(1000*10);
        return advice;
    }

//    @Bean
//    public RateLimiterRequestHandlerAdvice rateLimitAdvice() {
//        RateLimiterRequestHandlerAdvice advice = new RateLimiterRequestHandlerAdvice(
//                RateLimiterConfig.custom()
//                        .limitForPeriod(10)
//                        .timeoutDuration(Duration.ofMinutes(1))
//                        .build());
//        return advice;
//    }

    @Data
    @AllArgsConstructor
    static public class TickerInfo {
        private String ticker;
        private String name;
    }

    @Data
    @RequiredArgsConstructor
    static public class TickerNotFoundException extends Exception {
        final private String ticker;
    }
}
