package com.group7.ecommerce.service.impl;

import com.group7.ecommerce.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.email.from:noreply@ecommerce.com}")
    private String fromEmail;

    @Value("${app.name:E-Commerce}")
    private String appName;

    @Override
    @Async
    public void sendOtpEmail(String to, String otp) {
        try {
            SimpleMailMessage message = createBaseMessage(to);
            message.setSubject("Mã xác nhận đăng ký tài khoản");
            message.setText(buildOtpEmailContent(otp));

            mailSender.send(message);
            log.info("OTP email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send OTP email to: {}", to, e);
            throw new RuntimeException("Không thể gửi email OTP", e);
        }
    }

    @Override
    @Async
    public void sendWelcomeEmail(String to, String fullName) {
        try {
            SimpleMailMessage message = createBaseMessage(to);
            message.setSubject("Chào mừng bạn đến với " + appName);
            message.setText(buildWelcomeEmailContent(fullName));

            mailSender.send(message);
            log.info("Welcome email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send welcome email to: {}", to, e);
            throw new RuntimeException("Không thể gửi email chào mừng", e);
        }
    }

    @Override
    @Async
    public void sendResetPasswordEmail(String to, String resetToken) {
        try {
            SimpleMailMessage message = createBaseMessage(to);
            message.setSubject("Yêu cầu đặt lại mật khẩu");
            message.setText(buildResetPasswordEmailContent(resetToken));

            mailSender.send(message);
            log.info("Reset password email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send reset password email to: {}", to, e);
            throw new RuntimeException("Không thể gửi email đặt lại mật khẩu", e);
        }
    }

    // Private helper methods

    private SimpleMailMessage createBaseMessage(String to) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        return message;
    }

    private String buildOtpEmailContent(String otp) {
        return String.format(
                "Xin chào,\n\n" +
                        "Mã OTP của bạn là: %s\n\n" +
                        "Mã này có hiệu lực trong 5 phút.\n\n" +
                        "Nếu bạn không yêu cầu đăng ký tài khoản, vui lòng bỏ qua email này.\n\n" +
                        "Trân trọng,\n" +
                        "Đội ngũ %s",
                otp, appName
        );
    }

    private String buildWelcomeEmailContent(String fullName) {
        return String.format(
                "Xin chào %s,\n\n" +
                        "Chào mừng bạn đến với %s!\n\n" +
                        "Tài khoản của bạn đã được kích hoạt thành công. " +
                        "Bạn có thể bắt đầu sử dụng dịch vụ của chúng tôi ngay bây giờ.\n\n" +
                        "Nếu bạn có bất kỳ câu hỏi nào, đừng ngần ngại liên hệ với chúng tôi.\n\n" +
                        "Trân trọng,\n" +
                        "Đội ngũ %s",
                fullName, appName, appName
        );
    }

    private String buildResetPasswordEmailContent(String resetToken) {
        return String.format(
                "Xin chào,\n\n" +
                        "Chúng tôi nhận được yêu cầu đặt lại mật khẩu cho tài khoản của bạn.\n\n" +
                        "Mã xác nhận: %s\n\n" +
                        "Mã này có hiệu lực trong 15 phút.\n\n" +
                        "Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.\n\n" +
                        "Trân trọng,\n" +
                        "Đội ngũ %s",
                resetToken, appName
        );
    }
}