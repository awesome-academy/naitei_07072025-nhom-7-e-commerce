package com.group7.ecommerce.config;

import com.group7.ecommerce.utils.interceptor.AdminSessionInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final AdminSessionInterceptor adminSessionInterceptor;

    @Value("${app.default-product-image-url:/static/images/product-default.jpg}")
    private String defaultProductImageUrl;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(adminSessionInterceptor)
                .addPathPatterns("/**");
    }
    
    @Bean("defaultProductImageUrl")
    public String defaultProductImageUrl() {
        return defaultProductImageUrl;
    }
    
}
