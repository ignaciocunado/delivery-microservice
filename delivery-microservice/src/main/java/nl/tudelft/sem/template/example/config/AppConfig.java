package nl.tudelft.sem.template.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {
    @lombok.Generated
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
