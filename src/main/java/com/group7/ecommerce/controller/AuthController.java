package com.group7.ecommerce.controller;

import com.group7.ecommerce.dto.request.LoginDto;
import com.group7.ecommerce.dto.request.UserRegistrationDto;
import com.group7.ecommerce.dto.request.VerifyOtpDto;
import com.group7.ecommerce.dto.response.ApiResponse;
import com.group7.ecommerce.dto.response.JwtResponse;
import com.group7.ecommerce.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final UserService userService;
    private final MessageSource messageSource;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(
            @Valid @RequestBody UserRegistrationDto dto,
            Locale locale) {
        String message = userService.registerUser(dto);
        String successMessage = messageSource.getMessage("auth.register.success", null, locale);
        return ResponseEntity.ok(ApiResponse.success(successMessage, message));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<String>> verifyOtp(
            @Valid @RequestBody VerifyOtpDto dto,
            Locale locale) {
        String message = userService.verifyOtp(dto);
        String successMessage = messageSource.getMessage("auth.verify.otp.success", null, locale);
        return ResponseEntity.ok(ApiResponse.success(successMessage, message));
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<ApiResponse<String>> resendOtp(
            @RequestParam String email,
            Locale locale) {
        String message = userService.resendOtp(email);
        String successMessage = messageSource.getMessage("auth.resend.otp.success", null, locale);
        return ResponseEntity.ok(ApiResponse.success(successMessage, message));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtResponse>> login(
            @Valid @RequestBody LoginDto loginDto,
            Locale locale) {
        JwtResponse jwtResponse = userService.authenticateUser(loginDto);
        String successMessage = messageSource.getMessage("auth.login.success", null, locale);
        return ResponseEntity.ok(ApiResponse.success(successMessage, jwtResponse));
    }

    @GetMapping("/home")
    public ResponseEntity<String> demo(Locale locale){
        String message = messageSource.getMessage("page.home.welcome", null, locale);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/product")
    public ResponseEntity<String> products(Locale locale){
        String message = messageSource.getMessage("page.product.welcome", null, locale);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/order")
    public ResponseEntity<String> orders(Locale locale){
        String message = messageSource.getMessage("page.order.welcome", null, locale);
        return ResponseEntity.ok(message);
    }
}
