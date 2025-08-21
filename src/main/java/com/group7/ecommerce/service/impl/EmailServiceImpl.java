package com.group7.ecommerce.service.impl;

import com.group7.ecommerce.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final MessageSource messageSource;

    @Value("${app.email.from:noreply@ecommerce.com}")
    private String fromEmail;

    @Value("${app.name:E-Commerce}")
    private String appName;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    @Override
    public void sendOtpEmail(String to, String otp) {
        try {
            Locale locale = LocaleContextHolder.getLocale();

            // Prepare template variables
            Context context = new Context(locale);
            context.setVariable("recipientName", extractNameFromEmail(to));
            context.setVariable("otp", otp);
            context.setVariable("appName", appName);
            context.setVariable("frontendUrl", frontendUrl);
            context.setVariable("expiryMinutes", 5);

            // Generate HTML content from template
            String htmlContent = templateEngine.process("emails/otp-verification", context);

            // Get localized subject
            String subject = messageSource.getMessage(
                    "email.otp.subject",
                    new Object[]{appName},
                    locale
            );

            sendHtmlEmail(to, subject, htmlContent);
            log.info("OTP email sent successfully to: {}", to);

        } catch (Exception e) {
            log.error("Failed to send OTP email to: {}", to, e);
            throw new RuntimeException(
                    messageSource.getMessage("email.send.error", null, LocaleContextHolder.getLocale()),
                    e
            );
        }
    }

    @Override
    @Async
    public void sendWelcomeEmail(String to, String fullName) {
        try {
            Locale locale = LocaleContextHolder.getLocale();

            Context context = new Context(locale);
            context.setVariable("recipientName", fullName);
            context.setVariable("appName", appName);
            context.setVariable("frontendUrl", frontendUrl);
            context.setVariable("dashboardUrl", frontendUrl + "/dashboard");
            context.setVariable("supportEmail", "support@ecommerce.com");

            String htmlContent = templateEngine.process("emails/welcome", context);

            String subject = messageSource.getMessage(
                    "email.welcome.subject",
                    new Object[]{appName},
                    locale
            );

            sendHtmlEmail(to, subject, htmlContent);
            log.info("Welcome email sent successfully to: {}", to);

        } catch (Exception e) {
            log.error("Failed to send welcome email to: {}", to, e);
            throw new RuntimeException(
                    messageSource.getMessage("email.send.error", null, LocaleContextHolder.getLocale()),
                    e
            );
        }
    }

    @Override
    @Async
    public void sendResetPasswordEmail(String to, String resetToken) {
        try {
            Locale locale = LocaleContextHolder.getLocale();

            Context context = new Context(locale);
            context.setVariable("recipientName", extractNameFromEmail(to));
            context.setVariable("resetToken", resetToken);
            context.setVariable("resetUrl", frontendUrl + "/reset-password?token=" + resetToken);
            context.setVariable("appName", appName);
            context.setVariable("frontendUrl", frontendUrl);
            context.setVariable("expiryMinutes", 15);

            String htmlContent = templateEngine.process("emails/reset-password", context);

            String subject = messageSource.getMessage(
                    "email.reset.subject",
                    null,
                    locale
            );

            sendHtmlEmail(to, subject, htmlContent);
            log.info("Reset password email sent successfully to: {}", to);

        } catch (Exception e) {
            log.error("Failed to send reset password email to: {}", to, e);
            throw new RuntimeException(
                    messageSource.getMessage("email.send.error", null, LocaleContextHolder.getLocale()),
                    e
            );
        }
    }

    /**
     * Send HTML email with proper encoding
     */
    private void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true); // true = isHtml

        mailSender.send(message);
    }

    /**
     * Extract name from email for personalization
     */
    private String extractNameFromEmail(String email) {
        if (email == null || !email.contains("@")) {
            return "Bạn";
        }

        try {
            String localPart = email.substring(0, email.indexOf("@"));

            // Convert dots and underscores to spaces and capitalize
            String name = localPart.replace(".", " ").replace("_", " ");

            // Capitalize first letter of each word
            return capitalizeWords(name);

        } catch (Exception e) {
            log.warn("Error extracting name from email: {}", email, e);
            return "Bạn";
        }
    }

    /**
     * Capitalize first letter of each word
     */
    private String capitalizeWords(String text) {
        if (text == null || text.trim().isEmpty()) {
            return "Bạn";
        }

        String[] words = text.toLowerCase().trim().split("\\s+");
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)));
                if (word.length() > 1) {
                    result.append(word.substring(1));
                }
            }
            if (i < words.length - 1) {
                result.append(" ");
            }
        }

        return result.toString().isEmpty() ? "Bạn" : result.toString();
    }
}
