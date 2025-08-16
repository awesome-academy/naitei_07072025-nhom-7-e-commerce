package com.group7.ecommerce.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Async
    public void sendOtpEmail(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@yourapp.com");
        message.setTo(to);
        message.setSubject("Mã xác nhận đăng ký tài khoản");
        message.setText("Xin chào,\n\n" +
                "Mã OTP của bạn là: " + otp + "\n\n" +
                "Mã này có hiệu lực trong 5 phút.\n\n" +
                "Nếu bạn không yêu cầu đăng ký tài khoản, vui lòng bỏ qua email này.\n\n" +
                "Trân trọng,\nĐội ngũ hỗ trợ");

        mailSender.send(message);
    }
}