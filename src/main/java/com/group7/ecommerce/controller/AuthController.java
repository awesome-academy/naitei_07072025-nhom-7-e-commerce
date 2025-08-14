package com.group7.ecommerce.controller;

import com.group7.ecommerce.dto.request.LoginRequest;
import com.group7.ecommerce.dto.request.UserRegistrationDto;
import com.group7.ecommerce.dto.request.VerifyOtpDto;
import com.group7.ecommerce.dto.response.ApiResponse;
import com.group7.ecommerce.dto.response.LoginResponse;
import com.group7.ecommerce.dto.response.MessageResponse;
import com.group7.ecommerce.entity.User;
import com.group7.ecommerce.repository.UserRepository;
import com.group7.ecommerce.service.UserService;
import com.group7.ecommerce.utils.CustomUserPrincipal;
import com.group7.ecommerce.utils.JwtUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(
            @Valid @RequestBody UserRegistrationDto dto,
            BindingResult result) {

        if (result.hasErrors()) {
            List<String> errors = result.getFieldErrors()
                    .stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.toList());

            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Dữ liệu không hợp lệ", errors));
        }

        try {
            String message = userService.registerUser(dto);
            return ResponseEntity.ok(
                    ApiResponse.success("Đăng ký thành công", message)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Đã xảy ra lỗi: " + e.getMessage()));
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<String>> verifyOtp(
            @Valid @RequestBody VerifyOtpDto dto,
            BindingResult result) {

        if (result.hasErrors()) {
            List<String> errors = result.getFieldErrors()
                    .stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.toList());

            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Dữ liệu không hợp lệ", errors));
        }

        try {
            String message = userService.verifyOtp(dto);
            return ResponseEntity.ok(
                    ApiResponse.success("Xác thực OTP thành công", message)
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("OTP không hợp lệ hoặc đã hết hạn"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Đã xảy ra lỗi: " + e.getMessage()));
        }
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<ApiResponse<String>> resendOtp(@RequestParam String email) {
        try {
            String message = userService.resendOtp(email);
            return ResponseEntity.ok(
                    ApiResponse.success("Gửi lại OTP thành công", message)
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Email không tồn tại hoặc đã được xác thực"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Không thể gửi OTP: " + e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            String usernameOrEmail = loginRequest.getUsernameOrEmail();

            // Tìm user theo username hoặc email
            User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản với thông tin: " + usernameOrEmail));

            // Kiểm tra tài khoản có được kích hoạt không
            if (!user.isEmailVerified()) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Tài khoản chưa được kích hoạt hoặc đã bị khóa!"));
            }

            System.out.println("Raw password: " + loginRequest.getPassword());
            System.out.println("Encoded password in DB: " + user.getPassword());
            System.out.println("Matches? " + passwordEncoder.matches(loginRequest.getPassword(), user.getPassword()));
            // Kiểm tra mật khẩu
            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {

                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Sai mật khẩu!"));
            }

            User u = userRepository.findByUsernameOrEmail(loginRequest.getUsernameOrEmail(), loginRequest.getUsernameOrEmail()).get();
            System.out.println("User found: " + u);

            if (user == null) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Không tìm thấy user với username/email này!"));
            }

            // Xác thực với Spring Security
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsernameOrEmail(),
                            loginRequest.getPassword()
                    )
            );

// Lưu vào SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);

            System.out.println("authentication.getPrincipal() = " + authentication.getPrincipal());
            System.out.println("authentication.getPrincipal() class = " + authentication.getPrincipal().getClass());

// Lấy principal từ Authentication (đã qua CustomUserDetailsService)
            CustomUserPrincipal userPrincipal = (CustomUserPrincipal) authentication.getPrincipal();

// Tạo JWT từ username của principal
            String jwt = jwtUtils.generateTokenFromUsername(userPrincipal.getUsername());

            return ResponseEntity.ok(new LoginResponse(
                    jwt,
                    userPrincipal.getUsername(),
                    userPrincipal.getEmail(),
                    userPrincipal.getFullName(),
                    userPrincipal.getPhone(),
                    userPrincipal.getRole().name()
            ));

        } catch (BadCredentialsException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Sai username/email hoặc mật khẩu!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Lỗi đăng nhập: " + e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        SecurityContextHolder.getContext().setAuthentication(null);
        return ResponseEntity.ok(new MessageResponse("Đăng xuất thành công!"));
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("Chưa đăng nhập"));
        }

        CustomUserPrincipal userPrincipal = (CustomUserPrincipal) authentication.getPrincipal();

        Map<String, Object> profile = new HashMap<>();
        profile.put("id", userPrincipal.getId());
        profile.put("username", userPrincipal.getUsername());
        profile.put("email", userPrincipal.getEmail());
        profile.put("fullName", userPrincipal.getFullName());
        profile.put("phone", userPrincipal.getPhone());
        profile.put("address", userPrincipal.getAddress());
        profile.put("role", userPrincipal.getRole().name());
        profile.put("isActive", userPrincipal.isActive());

        return ResponseEntity.ok(profile);
    }

    // API kiểm tra quyền admin
    @GetMapping("/admin/check")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> checkAdminAccess() {
        return ResponseEntity.ok(new MessageResponse("Bạn có quyền admin!"));
    }

    // API dành cho user thường
    @GetMapping("/user/check")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> checkUserAccess() {
        return ResponseEntity.ok(new MessageResponse("Bạn có quyền truy cập user!"));
    }

    // API lấy thông tin user hiện tại
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("Chưa đăng nhập"));
        }

        CustomUserPrincipal userPrincipal = (CustomUserPrincipal) authentication.getPrincipal();
        return ResponseEntity.ok(userPrincipal);
    }
}