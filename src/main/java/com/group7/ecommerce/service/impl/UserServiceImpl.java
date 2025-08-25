package com.group7.ecommerce.service.impl;

import com.group7.ecommerce.dto.request.LoginDto;
import com.group7.ecommerce.dto.request.UserRegistrationDto;
import com.group7.ecommerce.dto.request.VerifyOtpDto;
import com.group7.ecommerce.dto.response.JwtResponse;
import com.group7.ecommerce.dto.response.ShowProfileResponse;
import com.group7.ecommerce.entity.User;
import com.group7.ecommerce.utils.CustomUserDetails;
import com.group7.ecommerce.utils.JwtUtils;
import com.group7.ecommerce.utils.constant.message.SuccessMessages;
import com.group7.ecommerce.utils.helper.OrderHelper;
import com.group7.ecommerce.utils.helper.ProductHelper;
import com.group7.ecommerce.utils.helper.UserHelper;
import com.group7.ecommerce.utils.validator.UserValidator;
import com.group7.ecommerce.repository.UserRepository;
import com.group7.ecommerce.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserValidator userValidator;
    private final UserHelper userHelper;
    private final OrderHelper orderHelper;
    private final ProductHelper productHelper;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

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

    @Override
    public JwtResponse authenticateUser(LoginDto loginDto) {

        // Validate input
        userValidator.validateLoginCredentials(loginDto);

        // Find user first to validate existence and status
        User user = userHelper.findUserByEmailOrUsernameOrThrow(loginDto.getEmailOrUsername());

        // Validate user account status
        userValidator.validateUserAccountActive(user);

        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getEmailOrUsername(),
                        loginDto.getPassword()));

        // Set authentication context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate JWT token
        String jwt = jwtUtils.generateJwtToken(authentication);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        // Return JWT response with user information
        return new JwtResponse(jwt, user.getEmail(), user.getUsername(), user.getFullName());
    }

    @Override
    public ShowProfileResponse showProfileAdmin(JwtResponse currentUser){

        User admin = userHelper.findUserByEmailOrThrow(currentUser.getEmail());

        // Lấy các thống kê cần thiết cho template
        long totalUsers = userHelper.getTotalUsers();
        long totalProducts = productHelper.getTotalProducts();
        long totalOrders = orderHelper.getTotalOrders();
        BigDecimal totalRevenue = orderHelper.getTotalRevenue();

        // Quick stats
        long pendingOrders = orderHelper.countPendingOrders();
        return new ShowProfileResponse(admin, totalUsers, totalProducts, totalOrders, totalRevenue, pendingOrders);
    }

}
