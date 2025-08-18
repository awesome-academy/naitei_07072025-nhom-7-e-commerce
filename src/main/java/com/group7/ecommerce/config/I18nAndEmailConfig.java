package com.group7.ecommerce.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.util.Arrays;
import java.util.Locale;

@Configuration
@EnableAsync
public class I18nAndEmailConfig implements WebMvcConfigurer {

    /**
     * Bean MessageSource chính cho cả web và email
     * Sử dụng ReloadableResourceBundleMessageSource để có hiệu suất và caching tốt hơn
     */
    @Bean
    @Primary
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource =
                new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setCacheSeconds(300); // Cache trong 5 phút
        messageSource.setFallbackToSystemLocale(false);
        messageSource.setUseCodeAsDefaultMessage(true); // Trả về key nếu không tìm thấy message
        return messageSource;
    }

    /**
     * LocaleResolver cho các web request
     * Sử dụng AcceptHeaderLocaleResolver để hỗ trợ cả Accept-Language header và query param
     */
    @Bean
    @Primary
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
        localeResolver.setSupportedLocales(Arrays.asList(
                new Locale("vi", "VN"), // Tiếng Việt
                new Locale("en", "US")  // Tiếng Anh
        ));
        localeResolver.setDefaultLocale(new Locale("vi", "VN")); // Mặc định tiếng Việt
        return localeResolver;
    }

    /**
     * Interceptor cho phép chuyển đổi ngôn ngữ qua query parameter
     */
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("lang"); // ?lang=en hoặc ?lang=vi
        return interceptor;
    }

    /**
     * Đăng ký locale change interceptor
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }

    // ========== CẤU HÌNH EMAIL TEMPLATE ==========

    /**
     * Template resolver cho các email template
     */
    @Bean
    public ITemplateResolver emailTemplateResolver() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setCacheable(true);
        templateResolver.setCacheTTLMs(3600000L); // Cache 1 giờ
        templateResolver.setOrder(1);
        return templateResolver;
    }

    /**
     * Template engine để xử lý email templates
     * Sử dụng cùng MessageSource để hỗ trợ i18n trong email
     */
    @Bean
    public TemplateEngine emailTemplateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(emailTemplateResolver());
        templateEngine.setEnableSpringELCompiler(true);
        // MessageSource sẽ được Spring tự động inject
        return templateEngine;
    }
}
