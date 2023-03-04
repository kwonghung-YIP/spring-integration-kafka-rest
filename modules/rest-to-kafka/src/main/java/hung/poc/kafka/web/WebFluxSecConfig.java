package hung.poc.kafka.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class WebFluxSecConfig {
    @Bean
    public SecurityWebFilterChain apiSecurityConfig(ServerHttpSecurity http) {
        return http.csrf(csrf -> csrf.disable())
                   .authorizeExchange(ex ->
                      ex.pathMatchers(HttpMethod.GET,"/actuator/**").permitAll()
                        .pathMatchers(HttpMethod.POST,"/api/customer").hasRole("user")
                        .pathMatchers(HttpMethod.POST,"/api/uppercase").hasRole("user")
                        .pathMatchers(HttpMethod.GET,"/api/ticker").permitAll()
                        .anyExchange().authenticated())
//                           ex.anyExchange().permitAll())
                   .httpBasic().and()
                   .build();
    }
}
