package com.group7.ecommerce.service.impl;

import com.group7.ecommerce.dto.request.UserRegistrationDto;
import com.group7.ecommerce.dto.request.VerifyOtpDto;
import com.group7.ecommerce.entity.User;
import com.group7.ecommerce.utils.constant.message.SuccessMessages;
import com.group7.ecommerce.utils.helper.UserHelper;
import com.group7.ecommerce.utils.validator.UserValidator;
import com.group7.ecommerce.repository.UserRepository;
import com.group7.ecommerce.service.EmailService;
import com.group7.ecommerce.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final UserValidator userValidator;
    private final UserHelper userHelper;

    @Override
    public String registerUser(UserRegistrationDto dto) {
        // Validation
        userValidator.validateEmailNotExists(dto.getEmail());

        // Business logic
        User user = userHelper.createUserWithOtp(dto);
        userRepository.save(user);

        // Send notification using User object
        userHelper.sendOtpToUser(user);

        return SuccessMessages.REGISTRATION_SUCCESS;
    }

    @Override
    public String verifyOtp(VerifyOtpDto dto) {
        // Find user
        User user = userHelper.findUserByEmailOrThrow(dto.getEmail());

        // Validations
        userValidator.validateOtpNotExpired(user);
        userValidator.validateOtpMatches(dto.getOtp(), user.getVerificationToken());

        // Business logic
        userHelper.activateUserAccount(user);

        return SuccessMessages.VERIFICATION_SUCCESS;
    }

    @Override
    public String resendOtp(String email) {
        // Find user
        User user = userHelper.findUserByEmailOrThrow(email);

        // Validation
        userValidator.validateEmailNotVerified(user);

        // Business logic - update và gửi OTP sử dụng User object
        userHelper.updateUserWithNewOtp(user);

        return SuccessMessages.RESEND_OTP_SUCCESS;
    }
}