package com.ssafy.house.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Zipkok API", version = "v1", description = "SSAFY Zipkok Web 서비스 API 문서"))
public class SwaggerConfig {
    // 기본 설정만으로 /swagger-ui.html, /v3/api-docs 엔드포인트가 활성화됩니다.
}
