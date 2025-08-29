package com.group7.ecommerce.service;

import com.group7.ecommerce.dto.request.*;
import com.group7.ecommerce.dto.response.JwtResponse;
import com.group7.ecommerce.dto.response.ShowProfileResponse;
import com.group7.ecommerce.dto.response.UpdateProfileResponse;

import java.util.Locale;

public interface UserService {

    /**
     * Đăng ký user mới
     * @param dto thông tin đăng ký
     * @return thông báo kết quả
     */
    String registerUser(UserRegistrationDto dto);

    /**
     * Xác thực OTP
     * @param dto thông tin OTP
     * @return thông báo kết quả
     */
    String verifyOtp(VerifyOtpDto dto);

    /**
     * Gửi lại OTP
     * @param email email cần gửi lại OTP
     * @return thông báo kết quả
     */
    String resendOtp(String email);

    /**
     * Đăng nhập
     * @param loginDto email cần gửi lại OTP
     * @return JWT response chứa token và thông tin user
     */
    JwtResponse authenticateUser(LoginDto loginDto);

    /**
     * Show Profile
     * @param currentUser thông tin người dùng hiện tại
     */
    ShowProfileResponse showProfileAdmin(JwtResponse currentUser);

    /**
     * Cập nhật thông tin cá nhân admin (không cho phép sửa username và email)
     */
    UpdateProfileResponse updateProfileAdmin(JwtResponse currentUser, UpdateProfileRequest request);

    /**
     * Change user password with locale support
     * @param email user email
     * @param changePasswordDto request change password
     * @param locale locale for error messages
     * @throws Exception if old password is incorrect or user not found
     */
    void changePassword(String email, ChangePasswordDto changePasswordDto, Locale locale) throws Exception;
}
