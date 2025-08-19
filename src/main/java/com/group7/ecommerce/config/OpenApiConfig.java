package com.group7.ecommerce.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.List;

/**
 * Configuration cho OpenAPI/Swagger documentation
 * Tuân thủ nguyên tắc Single Responsibility Principle (SRP) của SOLID
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("E-Commerce API")
                .description("API documentation E-Commerce")
                .version("1.0.0")
                .contact(new Contact()
                    .name("Group 7")
                    .email("group7@example.com"))
                .license(new License()
                    .name("MIT License")
                    .url("https://opensource.org/licenses/MIT")))
            .servers(List.of(
                new Server()
                    .url("http://localhost:8080")
                    .description("Development Server")
            ));
    }

    @Bean
    public OpenApiCustomizer i18nOpenApiCustomiser(MessageSource messageSource) {
        return openApi -> {
            var locale = LocaleContextHolder.getLocale();
            if (openApi.getPaths() != null) {
                openApi.getPaths().values().forEach(pathItem ->
                    pathItem.readOperations().forEach(operation -> {
                        // Translate summary/description if they are keys
                        if (operation.getSummary() != null) {
                            operation.setSummary(messageSource.getMessage(operation.getSummary(), null, operation.getSummary(), locale));
                        }
                        if (operation.getDescription() != null) {
                            operation.setDescription(messageSource.getMessage(operation.getDescription(), null, operation.getDescription(), locale));
                        }
                        // Translate parameters
                        if (operation.getParameters() != null) {
                            operation.getParameters().forEach(param -> {
                                if (param.getDescription() != null) {
                                    param.setDescription(messageSource.getMessage(param.getDescription(), null, param.getDescription(), locale));
                                }
                            });
                        }
                        // Translate responses descriptions
                        if (operation.getResponses() != null) {
                            operation.getResponses().forEach((code, apiResponse) -> {
                                if (apiResponse.getDescription() != null) {
                                    apiResponse.setDescription(messageSource.getMessage(apiResponse.getDescription(), null, apiResponse.getDescription(), locale));
                                }
                            });
                        }
                    })
                );
            }
        };
    }
}
