package hung.poc.kafka.cloudstream;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

@Slf4j
@Configuration
public class SpringCloudStreamConfig {

    @Bean
    public Function<String,String> uppercase() {
        return (s) -> {
            log.info("Received payload: {}",s);
            return "~"+s.toUpperCase()+"~";
        };
    }
}
