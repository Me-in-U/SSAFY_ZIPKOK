package com.ssafy.house.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@Configuration
@ControllerAdvice
public class ApiKeyConfig {
    @Value("${api.key_vworld}")
    private String apiKeyVworld;

    @Value("${api.key_sgis_service_id}")
    private String apiKeySgisServiceId;

    @Value("${api.key_sgis_security}")
    private String apiKeySgisSecurity;

    @Value("${api.key_data}")
    private String apiKeyData;

    @ModelAttribute
    public void addApiKeys(Model model) {
        model.addAttribute("apiKeyVworld", apiKeyVworld);
        model.addAttribute("apiKeySgisServiceId", apiKeySgisServiceId);
        model.addAttribute("apiKeySgisSecurity", apiKeySgisSecurity);
        model.addAttribute("apiKeyData", apiKeyData);
    }
}
