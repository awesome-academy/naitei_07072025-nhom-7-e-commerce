package com.group7.ecommerce.utils.validator;

import com.group7.ecommerce.dto.request.LoginDto;
import com.group7.ecommerce.entity.User;
import com.group7.ecommerce.repository.UserRepository;
import com.group7.ecommerce.utils.constant.message.ErrorMessages;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class UserValidator {

    private final UserRepository userRepository;

    public void validateEmailNotExists(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(ErrorMessages.EMAIL_EXISTS);
        }
    }

    public void validateEmailNotVerified(User user) {
        if (user.isEmailVerified()) {
            throw new IllegalArgumentException(ErrorMessages.EMAIL_ALREADY_VERIFIED);
        }
    }

    public void validateOtpNotExpired(User user) {
        if (user.getTokenExpiry() == null || LocalDateTime.now().isAfter(user.getTokenExpiry())) {
            throw new IllegalArgumentException(ErrorMessages.OTP_EXPIRED);
        }
    }

    public void validateOtpMatches(String providedOtp, String storedOtp) {
        if (!providedOtp.equals(storedOtp)) {
            throw new IllegalArgumentException(ErrorMessages.OTP_INVALID);
        }
    }

    public void validateLoginCredentials(LoginDto loginDto) {
        if (loginDto == null) {
            throw new IllegalArgumentException(ErrorMessages.LOGIN_DATA_NULL);
        }
    }

    public void validateUserAccountActive(User user) {
        if (!user.isEmailVerified()) {
            throw new IllegalArgumentException(ErrorMessages.EMAIL_NOT_VERIFIED);
        }
    }

}
