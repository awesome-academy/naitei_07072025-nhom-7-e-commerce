package com.group7.ecommerce.utils.helper;

import com.group7.ecommerce.enums.Role;
import com.group7.ecommerce.utils.constant.UserConstants;
import com.group7.ecommerce.utils.constant.message.ErrorMessages;
import com.group7.ecommerce.dto.request.UserRegistrationDto;
import com.group7.ecommerce.entity.User;
import com.group7.ecommerce.repository.UserRepository;
import com.group7.ecommerce.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Random;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserHelper {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Tìm user theo email hoặc throw exception
     */
    public User findUserByEmailOrThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessages.USER_NOT_FOUND));
    }

    /**
     * Tạo user mới với OTP
     */
    public User createUserWithOtp(UserRegistrationDto dto) {
        String otp = generateOtp();
        return User.builder()
                .email(dto.getEmail())
                .fullName(dto.getFullName())
                .username(dto.getUsername())
                .phone(dto.getPhone())
                .password(passwordEncoder.encode(dto.getPassword()))
                .emailVerified(false)
                .role(Role.USER)
                .verificationToken(otp)
                .tokenExpiry(LocalDateTime.now().plusMinutes(UserConstants.OTP_EXPIRY_MINUTES))
                .build();
    }

    /**
     * Cập nhật user với OTP mới và gửi email
     */
    public void updateUserWithNewOtp(User user) {
        String otp = generateOtp();
        user.setVerificationToken(otp);
        user.setTokenExpiry(LocalDateTime.now().plusMinutes(UserConstants.OTP_EXPIRY_MINUTES));
        userRepository.save(user);

        // Gửi OTP sử dụng email từ user object
        sendOtpToUser(user);
    }

    /**
     * Gửi OTP đến user (sử dụng email từ User object)
     */
    public void sendOtpToUser(User user) {
        emailService.sendOtpEmail(user.getEmail(), user.getVerificationToken());
    }

    /**
     * Gửi OTP đến user (overload method for backward compatibility)
     */
    public void sendOtpToUser(String email, String otp) {
        emailService.sendOtpEmail(email, otp);
    }

    /**
     * Kích hoạt tài khoản user
     */
    public void activateUserAccount(User user) {
        user.setEmailVerified(true);
        user.setVerificationToken(null);
        user.setTokenExpiry(null);
        userRepository.save(user);

        // Gửi email chào mừng
        emailService.sendWelcomeEmail(user.getEmail(), user.getFullName());
    }

    /**
     * Generate OTP 6 chữ số
     */
    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    /**
     * Tìm kiếm Email hoặc email
     */
    public User findUserByEmailOrUsernameOrThrow(String emailOrUsername) {
        return userRepository.findByEmailOrUsername(emailOrUsername, emailOrUsername)
                .orElseThrow(() -> new UsernameNotFoundException("User not found after authentication"));
    }

    /**
     * Đếm tổng số user trong hệ thống
     */
    public long getTotalUsers() {
        try {
            return userRepository.count();
        } catch (Exception e) {
            log.error("Error getting total users count", e);
            return 0L;
        }
    }
}
