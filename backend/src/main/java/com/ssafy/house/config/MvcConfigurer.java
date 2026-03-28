package com.ssafy.house.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class MvcConfigurer {
    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
