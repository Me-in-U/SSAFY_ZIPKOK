package com.ssafy.house.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfigurer implements WebMvcConfigurer {
    @Value("${spring.servlet.multipart.location}")

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
