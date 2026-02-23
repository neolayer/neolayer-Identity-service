package com.neolayer.identity.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("NeoLayer Identity Service API")
                        .version("1.0.0")
                        .description("Complete Identity and Authentication Service for NeoLayer\n\n" +
                                "## Features\n" +
                                "- User registration and authentication\n" +
                                "- JWT token-based security\n" +
                                "- OAuth Client management\n" +
                                "- User profile management\n" +
                                "- Role-based access control\n\n" +
                                "## Getting Started\n" +
                                "1. Register a new user using `/auth/register`\n" +
                                "2. Login using `/auth/login` to get JWT token\n" +
                                "3. Use the token in Authorization header for authenticated endpoints\n" +
                                "4. Create OAuth clients for your applications")
                        .contact(new Contact()
                                .name("NeoLayer Team")
                                .url("https://github.com/neolayer")
                                .email("support@neolayer.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Enter JWT token")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"));
    }

}
