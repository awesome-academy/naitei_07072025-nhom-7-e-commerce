package com.group7.ecommerce.controller;

import com.group7.ecommerce.dto.request.UserRegistrationDto;
import com.group7.ecommerce.dto.request.VerifyOtpDto;
import com.group7.ecommerce.dto.response.ApiResponse;
import com.group7.ecommerce.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(
            @Valid @RequestBody UserRegistrationDto dto) {
        String message = userService.registerUser(dto);
        return ResponseEntity.ok(ApiResponse.success("Đăng ký thành công", message));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<String>> verifyOtp(
            @Valid @RequestBody VerifyOtpDto dto) {
        String message = userService.verifyOtp(dto);
        return ResponseEntity.ok(ApiResponse.success("Xác thực OTP thành công", message));
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<ApiResponse<String>> resendOtp(@RequestParam String email) {
        String message = userService.resendOtp(email);
        return ResponseEntity.ok(ApiResponse.success("Gửi lại OTP thành công", message));
    }
}
