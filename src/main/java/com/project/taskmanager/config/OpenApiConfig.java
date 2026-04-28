package com.project.taskmanager.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Task Manager API")
                        .description("Session tabanlı kimlik doğrulama. Önce POST /api/auth/login ile giriş yapın; JSESSIONID cookie otomatik set edilir.")
                        .version("1.0"));
    }
}
