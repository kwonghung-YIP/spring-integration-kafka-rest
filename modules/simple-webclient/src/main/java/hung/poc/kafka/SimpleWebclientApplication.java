package hung.poc.kafka;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;

import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Slf4j
@SpringBootApplication
public class SimpleWebclientApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimpleWebclientApplication.class, args);
    }

    @Value("${polygon.apikey}")
    private String apiKey;
    @Bean
    public ApplicationRunner simpleWebClient() {
        return (argv) -> {
            WebClient webclient = WebClient.builder()
                    .baseUrl("https://api.polygon.io")
                    .defaultHeader(HttpHeaders.AUTHORIZATION,"Bearer " + apiKey)
                    .build();

            String ticker = "q";

            CountDownLatch countdown = new CountDownLatch(1);

            Consumer<Throwable> errorConsumer = (e) -> { log.error("",e);};

            Runnable done = () -> countdown.countDown();

            webclient.get().uri(uriBuilder ->
                            uriBuilder.path("/v3/reference/tickers/{ticker}")
                                    //.queryParam("ticker","AAPL")
                                    .build(ticker))
                    .retrieve()
                    .onStatus(status -> status.isSameCodeAs(HttpStatus.NOT_FOUND), resp -> Mono.error(new TickerNotFoundException(ticker)))
                    .bodyToMono(new ParameterizedTypeReference<SingleResult<TickerInfo>>() {})
                    .onErrorReturn(TickerNotFoundException.class,new SingleResult<>())
                    .log()
                    .subscribe(s -> {
                        log.info("here : {}",s);
                    },errorConsumer,done);

            countdown.await(2000, TimeUnit.MILLISECONDS);
        };
    }

    @Data
    static public class ListResult<T> {

        private long count;

        @JsonAlias("request_id")
        private String requestId;

        private T[] results;

        private String status;
    }

    @Data
    static public class SingleResult<T> {
        @JsonAlias("request_id")
        private String requestId;
        private T results;
        private String status;
    }

    @Data
    //@JsonIgnoreProperties({"address","branding"})
    static public class TickerInfo {

        private String ticker;
        private String name;
        private Market market;
        private Locale locale;
        @JsonAlias({"primary_exchange"})
        private String primaryExchange;
        private String type;
        private Boolean active;
        @JsonAlias({"currency_name"})
        private String currency;
        //cik
        //composite_figi
        //share_class_figi
        @JsonAlias({"market_cap"})
        private Long marketCap;
        @JsonAlias({"phone_number"})
        private String phone;
        private Address address;
        private String description;

        //https://www.sec.gov/corpfin/division-of-corporation-finance-standard-industrial-classification-sic-code-list
        private SECIndustryCode sic;
        @JsonSetter("sic_code")
        public void setSicCode(String code) {
            if (sic==null) {
                sic = new SECIndustryCode();
            }
            sic.setCode(code);
        }
        @JsonSetter("sic_description")
        public void setSicDes(String desc) {
            if (sic==null) {
                sic = new SECIndustryCode();
            }
            sic.setDescription(desc);
        }

        //ticker_root
        //ticker_suffix
        @JsonAlias({"homepage_url"})
        private URL homepage;
        @JsonAlias({"total_employees"})
        private Long ttlEmployees;
        @JsonAlias({"list_date"})
        private LocalDate listDate;
        @JsonProperty("delisted_utc")
        private Optional<LocalDate> delistDate;
        private Branding branding;
        @JsonAlias({"share_class_shares_outstanding"})
        private Long shareClassSharesOS;
        @JsonAlias({"weighted_shares_outstanding"})
        private Long weightedSharesOS;
        @JsonAlias({"round_lot"})
        private Long lotSize;
    }

    enum Market {
        stocks,
        crypto,
        fx,
        otc
    }
    enum Locale {
        us,
        global
    }
    @Data
    static public class Address {

        @JsonAlias({"address1"})
        private String firstLine;

        private String city;

        private String state;

        @JsonAlias({"postal_code"})
        private String postalCode;
    }

    @Data
    static public class SECIndustryCode {
        private String code;
        private String description;
    }

    @Data
    static public class Branding {
        @JsonAlias({"logo_url"})
        private URL logoUrl;
        @JsonAlias({"icon_url"})
        private URL iconUrl;
    }

    @RequiredArgsConstructor
    static public class TickerNotFoundException extends Exception {
        final private String ticker;

    }
}
