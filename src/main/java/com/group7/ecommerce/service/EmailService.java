package com.group7.ecommerce.service;

public interface EmailService {

    /**
     * Gửi email OTP cho user
     * @param to email người nhận
     * @param otp mã OTP
     */
    void sendOtpEmail(String to, String otp);

    /**
     * Gửi email chào mừng sau khi đăng ký thành công
     * @param to email người nhận
     * @param fullName tên đầy đủ của user
     */
    void sendWelcomeEmail(String to, String fullName);

    /**
     * Gửi email reset password
     * @param to email người nhận
     * @param resetToken token reset password
     */
    void sendResetPasswordEmail(String to, String resetToken);
}
