package com.group7.ecommerce.service;

import com.group7.ecommerce.dto.request.UserRegistrationDto;
import com.group7.ecommerce.dto.request.VerifyOtpDto;
import com.group7.ecommerce.entity.User;
import com.group7.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String registerUser(UserRegistrationDto dto) {
        // Kiểm tra email đã tồn tại
        if (userRepository.existsByEmail(dto.getEmail())) {
            return "Email đã được đăng ký";
        }

        // Tạo user mới
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setFullName(dto.getFullName());
        user.setUsername(dto.getUsername());
        user.setPhone(dto.getPhone());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        // Tạo OTP
        String otp = generateOtp();
        user.setVerificationToken(otp);
        user.setTokenExpiry(LocalDateTime.now().plusMinutes(5)); // OTP có hiệu lực 5 phút

        userRepository.save(user);

        // Gửi OTP qua email
        emailService.sendOtpEmail(dto.getEmail(), otp);

        return "Đăng ký thành công! Vui lòng kiểm tra email để lấy mã OTP";
    }

    public String verifyOtp(VerifyOtpDto dto) {
        Optional<User> userOpt = userRepository.findByEmail(dto.getEmail());

        if (userOpt.isEmpty()) {
            return "Không tìm thấy tài khoản với email này";
        }

        User user = userOpt.get();

        // Kiểm tra OTP đã hết hạn
        if (user.getTokenExpiry() == null || LocalDateTime.now().isAfter(user.getTokenExpiry())) {
            return "OTP đã hết hạn";
        }

        // Kiểm tra OTP đúng
        if (!dto.getOtp().equals(user.getVerificationToken())) {
            return "OTP không đúng";
        }

        // Xác nhận email
        user.setEmailVerified(true);
        user.setVerificationToken(null);
        user.setTokenExpiry(null);
        userRepository.save(user);

        return "Xác nhận email thành công! Tài khoản đã được kích hoạt";
    }

    public String resendOtp(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            return "Không tìm thấy tài khoản với email này";
        }

        User user = userOpt.get();

        if (user.isEmailVerified()) {
            return "Email đã được xác nhận";
        }

        // Tạo OTP mới
        String otp = generateOtp();
        user.setVerificationToken(otp);
        user.setTokenExpiry(LocalDateTime.now().plusMinutes(5));

        userRepository.save(user);

        // Gửi OTP qua email
        emailService.sendOtpEmail(email, otp);

        return "OTP mới đã được gửi đến email của bạn";
    }

    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // Tạo số 6 chữ số
        return String.valueOf(otp);
    }
}
